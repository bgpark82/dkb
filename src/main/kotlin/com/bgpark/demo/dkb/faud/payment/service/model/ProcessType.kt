package com.bgpark.demo.dkb.faud.payment.service.model

enum class PaymentProcessType{
    UPDATE,
    CREATE;

    fun extractType(): ProcessType = when(this) {
        CREATE -> ProcessType.CREATE
        UPDATE -> ProcessType.UPDATE
    }
}

enum class ProcessType {
    UPDATE,
    CREATE
}
