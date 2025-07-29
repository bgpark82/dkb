package com.bgpark.demo.dkb.faud.payment.service.model

enum class MfaStatus(private val jsonValue: String) {
    CREATED("created"),
    SECOND_FA_PASSED("second_fa_passed"),
    AUTHORIZED("authorized"),
    EXPIRED("expired");
}
