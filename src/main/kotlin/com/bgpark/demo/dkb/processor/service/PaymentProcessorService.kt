package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.config.ProcessorConfigurationProperties
import com.bgpark.demo.dkb.processor.model.PaymentStatus
import com.bgpark.demo.dkb.processor.model.PaymentType
import com.bgpark.demo.dkb.processor.model.ProcessType
import io.micrometer.core.annotation.Timed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentProcessorService(
    val properties: ProcessorConfigurationProperties,
    val batchProcessor: BatchProcessorService,
    val mfaService: MfaService
) {
    private val logger = LoggerFactory.getLogger(PaymentProcessorService::class.java)

    @Timed(
        value = "processor.mfa-authorized", // 매트릭스 이름
        description = "Time taken to process MFA authorized SEPA instant payments",
        percentiles = [0.5, 0.95, 0.99], // 측정할 백분위
        histogram = true, // 히스토그램을 활성화하여 시간 분포를 기록
        extraTags = ["type", "mfa_authorized"]
    )
    fun processMfaAuthorized() {
        logger.info("Starting mfa authorized instant payment processor")
        val result = with(properties.mfaAuthorized) {
            batchProcessor.process(
                paymentStatus = PaymentStatus.MFA_AUTHORIZED,
                paymentType = PaymentType.SINGLE,
                processType = ProcessType.INSTANT,
                batchSize = batchSize,
            ) {
                mfaService.authorize(payment = it)
            }
        }
        println("Processed $result payments with status ${PaymentStatus.MFA_AUTHORIZED} and type ${PaymentType.SINGLE} using ${ProcessType.INSTANT} processing type.")
    }
}