package com.bgpark.demo.dkb.faud.payment.service.model

import com.fasterxml.jackson.annotation.JsonValue

enum class PaymentType(@JsonValue val jsonValue: String) {
    PAYMENT("payment"),
    SCHEDULED_PAYMENT("scheduled-payment"),
    RECURRING_PAYMENT("recurring-payment")
}