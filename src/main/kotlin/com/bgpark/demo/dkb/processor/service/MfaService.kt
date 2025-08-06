package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.common.i18n.MessageKeys
import com.bgpark.demo.dkb.processor.domain.Payment
import com.bgpark.demo.dkb.processor.dto.PaymentEvaluationStatus
import com.bgpark.demo.dkb.processor.model.PaymentStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MfaService(
    private val fraudService: FraudService,
) {

    val logger = LoggerFactory.getLogger(MfaService::class.java)

    fun authorize(payment: Payment) {
        try {
            val response = fraudService.evaluatePayment(payment)
            when (response.status) {
                PaymentEvaluationStatus.PROCEEDABLE -> {
                    logger.info("Payment ${payment.id} is proceedable from fraud service, status: ${response.status}")
                    payment.paymentStatus = PaymentStatus.MFA_AUTHORIZED
                } else -> {
                    logger.info("Payment ${payment.id} is not proceedable from fraud service, status: ${response.status}")
                    payment.paymentStatus = PaymentStatus.FRAUD_REJECTED
                    payment.messageKey = MessageKeys.ExceptionKeys.FRAUD_REJECTED.key
            }
        }

        } catch (e: Exception) {
            logger.info("MFA authorize failed: ${e.message}")
            payment.paymentStatus = PaymentStatus.FRAUD_REJECTED
            payment.messageKey = MessageKeys.ExceptionKeys.FRAUD_REJECTED.key
        }
    }
}