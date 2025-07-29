package com.bgpark.demo.dkb.faud.payment.service.model

class ResourceConstants private constructor() {
    internal object InstantSepaCreditTransferEvaluation {
        const val TYPE = "instantSepaCreditTransferEvaluation"
        const val PATH = PAYMENT_EVALUATION_PATH
    }

    companion object {
        private const val PAYMENT_EVALUATION_PATH = "/payment-evaluations"
    }
}


