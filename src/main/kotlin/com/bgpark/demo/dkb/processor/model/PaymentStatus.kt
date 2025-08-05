package com.bgpark.demo.dkb.processor.model

enum class PaymentStatus(
    val description: String,
    val phase: Phase
) {
    ACCEPTED("accepted", Phase.TERMINAL),
    FRAUD_REJECTED("fraud-rejected", Phase.TERMINAL),
    MFA_AUTHORIZED("mfa-authorized", Phase.INTERMEDIATE),
    ;

    enum class Phase {
        INITIAL, INTERMEDIATE, TERMINAL
    };

    fun isInTerminalStatus(): Boolean {
        return this in setOf(
            ACCEPTED
        )
    }
}