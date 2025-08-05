package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.domain.Payment
import com.bgpark.demo.dkb.processor.dto.PaymentEvaluationResponse
import com.bgpark.demo.dkb.processor.dto.PaymentEvaluationStatus
import org.springframework.stereotype.Service

@Service
class FraudService {

    fun evaluatePayment(payment: Payment): PaymentEvaluationResponse {
        return PaymentEvaluationResponse(status = PaymentEvaluationStatus.PROCEEDABLE)
    }
}