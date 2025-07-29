package com.bgpark.demo.dkb.faud.payment.service.model

import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class VerificationOfPayee(
    var id: UUID? = null,
    var osPlusIdentifier: String? = null,
    var result: VerificationOfPayeeResult? = null,
    var correctCreditorName: String? = null,
    var verificationNotPossibleReason: String? = null,
)

enum class VerificationOfPayeeResult(@JsonValue val value: String) {
    MATCH("match"),
    NO_MATCH("no-match"),
    VERIFICATION_NOT_POSSIBLE("verification-not-possible"),
    CLOSE_MATCH("close-match")
}