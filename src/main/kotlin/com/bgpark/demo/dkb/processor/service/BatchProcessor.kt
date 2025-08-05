package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.domain.PaymentRepository
import com.bgpark.demo.dkb.processor.model.PaymentStatus
import com.bgpark.demo.dkb.processor.model.PaymentType
import com.bgpark.demo.dkb.processor.model.ProcessType
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Future

@Service
class BatchProcessor(
    /**
     * @see org.springframework.transaction.support.TransactionTemplate.execute
     */
    private val transactionTemplate: TransactionTemplate,
    private val paymentRepository: PaymentRepository,
) {

    val logger = LoggerFactory.getLogger(BatchProcessor::class.java)!!
    /**
     * @see java.util.concurrent.Executor 인터페이스의 구현체
     * @see org.springframework.core.task.TaskExecutor 구현체, 스레드풀을 관리하고 비동기 작업을 수행
     * @see org.springframework.core.task.AsyncTaskExecutor
     * @see org.springframework.core.task.SimpleAsyncTaskExecutor 작업이 올때마다 새로운 스레드 생성, 스레드 재사용 안함
     * @see org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor (deprecated) 스레드 풀로 스레드 재사용
     *
     */
    // 100_000번 실행하는데 500ms
    private val virtualAsyncTaskExecutor = createAsyncTaskExecutor(true, "virtual-processor-")
    // 100_000번 실행하는데 6초 소요 (10배 느림)
    private val platformAsyncTaskExecutor = createAsyncTaskExecutor(false, "platform-processor-")

    private fun createAsyncTaskExecutor(isVirtualThread: Boolean, threadNamePrefix: String) =
        SimpleAsyncTaskExecutor().apply {
            /**
             * 가상 스레드 생성 비용은 매우 낮음
             * OS 스레드 자원을 고갈 시키지 않음
             * non-blocking I/O 작업에 적합: 가상 스레드가 I/O 작업을 기다리는 동안 다른 작업을 수행할 수 있음
             */
            setVirtualThreads(isVirtualThread)
            setThreadNamePrefix(threadNamePrefix)
        }

    fun process(
        processType: ProcessType,
        paymentType: PaymentType,
        paymentStatus: PaymentStatus,
        batchSize: Int,
        process: () -> Unit
    ) = (1..batchSize).map {
                virtualAsyncTaskExecutor.submit<Int> {
                    logger.info("Starting process")
                    transactionTemplate.execute<Int> {
                        paymentRepository.findNextFromInstantPaymentsByStatusAndPaymentType(
                            paymentStatus = paymentStatus,
                            paymentType = paymentType,
                        )?.let { payment ->
                            MDC.put("activityId", "activityId-${UUID.randomUUID()}")
                            val initialStatus = payment.paymentStatus
                            logger.info("Initial status: $initialStatus")
                            try {
                                process()
                            } catch (e: Exception) {
                                logger.error("Failed to process payment: ${payment.id} with status ${payment.paymentStatus}", e)
                            } finally {
                                paymentRepository.save(payment)
                                if (initialStatus != payment.paymentStatus && payment.paymentStatus?.isInTerminalStatus() == true) {
                                    // TODO: metric
                                    logger.info("record payment processing time for payment: ${payment.id} with status ${payment.paymentStatus}")
                                }
                            }
                            1
                        } ?: run {
                            logger.debug("No payment found for status ${paymentStatus} and type ${paymentType}")
                            0
                        }
                    }
                }
            }.sumOf { it.get() }




/*
    private fun processPayment(
        processType: ProcessType,
        paymentStatus: PaymentStatus,
        paymentType: PaymentType
    ): Int {
        val payment = when (processType) {
            ProcessType.INSTANT -> paymentRepository.findNextFromInstantPaymentsByStatusAndPaymentType(paymentStatus, paymentType)
            else -> null
        } ?: run {
            println("No payment found for status ${paymentStatus} and type ${paymentType}")
            return 0
        }
        return 1
    }
*/

}