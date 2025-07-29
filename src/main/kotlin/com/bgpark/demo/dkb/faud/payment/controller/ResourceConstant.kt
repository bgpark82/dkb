package com.bgpark.demo.dkb.faud.payment.controller

/**
 * class contains only constants
 */
class ResourceConstant private constructor() {

    internal object PaymentEvaluation {
        const val TYPE = "paymentEvaluation"
        const val PATH = PAYMENT_EVALUATION_PATH
    }

    companion object {
        const val PAYMENT_EVALUATION_PATH = "/payment-evaluation"
        const val APPLICATION_JSON_API_CONTENT_TYPE = "application/vnd.api+json"
    }
}