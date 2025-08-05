package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.domain.Payment
import com.bgpark.demo.dkb.processor.domain.PaymentRepository
import com.bgpark.demo.dkb.processor.model.PaymentStatus
import com.bgpark.demo.dkb.processor.model.PaymentType
import com.bgpark.demo.dkb.processor.model.ProcessType
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class BatchProcessorService(
    /**
     * @see org.springframework.transaction.support.TransactionTemplate.execute
     */
    private val transactionTemplate: TransactionTemplate,
    private val paymentRepository: PaymentRepository,
    private val meterRegistry: MeterRegistry,
) {

    val logger = LoggerFactory.getLogger(BatchProcessorService::class.java)!!
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
        process: (Payment) -> Unit
    ) = (1..batchSize).map {
                val contextMap = MDC.getCopyOfContextMap()
                virtualAsyncTaskExecutor.submit<Int> {

                    MDC.setContextMap(contextMap)
                    logger.info("Starting process")
                    transactionTemplate.execute<Int> {
                        processPayment(paymentStatus, paymentType, process)
                    }
                }
            }.sumOf { it.get() }

    private fun processPayment(
        paymentStatus: PaymentStatus,
        paymentType: PaymentType,
        process: (Payment) -> Unit
    ): Int = paymentRepository.findNextFromInstantPaymentsByStatusAndPaymentType(
            paymentStatus = paymentStatus,
            paymentType = paymentType,
        )?.let { payment ->
            val initialStatus = payment.paymentStatus
            logger.info("Initial status: $initialStatus")
            try {
                process(payment)
            } catch (e: Exception) {
                logger.error("Failed to process payment: ${payment.id} with status ${payment.paymentStatus}", e)
            } finally {
                paymentRepository.save(payment)
                publishMetric(initialStatus, payment, paymentStatus, paymentType)
            }
            1
        } ?: run {
            logger.info("No payment found for status ${paymentStatus} and type ${paymentType}")
            0
        }

    private fun publishMetric(
        initialStatus: PaymentStatus?,
        payment: Payment,
        paymentStatus: PaymentStatus,
        paymentType: PaymentType
    ) {
        if (initialStatus != payment.paymentStatus && payment.paymentStatus?.isInTerminalStatus() == true) {
            logger.info("record payment processing time for payment: ${payment.id} with status ${payment.paymentStatus}")
            recordProcessTime(
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                mfaAuthorizedAt = LocalDateTime.now(),
                payment = Payment(
                    id = Random.nextLong(),
                    paymentStatus = paymentStatus,
                    paymentType = paymentType,
                    isInstantPayment = true,
                    amount = BigDecimal.ONE,
                    updatedAt = LocalDateTime.now(),
                )
            )
        }
    }

    fun recordProcessTime(
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
        mfaAuthorizedAt: LocalDateTime,
        payment: Payment
    ) {
        logger.info("Recording process time for payment: ${payment.id}")
        val totalProcessDuration = Duration.between(createdAt, updatedAt)
        val totalBusinessProcessDuration = Duration.between(mfaAuthorizedAt, updatedAt)

        // 카운터 매트릭스 생성
        val tags = Tags.of(
            Tag.of("status", payment.paymentStatus?.name ?: "UNKNOWN"),
            Tag.of("type", payment.paymentType?.name ?: "UNKNOWN"),
            Tag.of("isInstantPayment", payment.isInstantPayment.toString()),
        )

        // metric에 tag와 값을 추가
        meterRegistry.counter("payment.processed", tags).increment()

        meterRegistry.timer("payment.processing.time", tags).record(totalProcessDuration)

        Timer.builder("payment.processing.time.from.mfa.authorized")
            .tags(tags)
            .description("Total time taken to process payment starting from creation datetime")
            .publishPercentileHistogram(true)
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry)
            .record(totalBusinessProcessDuration)
    }
}