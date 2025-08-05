package com.bgpark.demo.dkb.processor.dto

data class PaymentEvaluationResponse(val status: PaymentEvaluationStatus)

enum class PaymentEvaluationStatus {
    PROCEEDABLE,
    REJECTED,
}