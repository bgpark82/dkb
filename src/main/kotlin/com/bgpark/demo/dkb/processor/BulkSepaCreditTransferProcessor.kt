package com.bgpark.demo.dkb.processor

import com.bgpark.demo.dkb.processor.domain.Payment
import com.bgpark.demo.dkb.processor.domain.PaymentRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import kotlin.random.Random

@Component
@EnableScheduling
@ConditionalOnProperty(
    name = ["processor.enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class BulkSepaCreditTransferProcessor(
    private val transactionTemplate: TransactionTemplate,
    private val paymentRepository: PaymentRepository,
) {

    private val asyncTaskExecutor: AsyncTaskExecutor = SimpleAsyncTaskExecutor().apply {
        setThreadNamePrefix("bulk-sepa-credit-transfer-")
        setVirtualThreads(true)
    }

    @Scheduled(
        fixedRateString = "\${processor.scheduled.fixedDelay}",
    )
    fun processBulkSepaCreditTransfer() {
        println("Start processing")
        val result = (1..10).map {
            asyncTaskExecutor.submit<Int> {
                println("Thread: ${Thread.currentThread().name}, Number: $it")
                transactionTemplate.execute<Int> {
                    val amount = BigDecimal.valueOf(Random.nextLong(100))
                    println("amount=$amount")
                    val savedPayment = paymentRepository.save(Payment(amount = amount))
                    savedPayment.amount.intValueExact()
                }
            }
        }.sumOf { it.get() }
        println("Complete processing, result=$result")
    }
}