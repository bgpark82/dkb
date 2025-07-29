package com.bgpark.demo.dkb.faud.payment.controller.dto

import com.bgpark.demo.dkb.faud.payment.controller.dto.PaymentEvaluationDocumentStatus.IN_PROGRESS
import com.bgpark.demo.dkb.faud.payment.controller.dto.PaymentProcessType.*
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class InstantSepaPaymentEvaluationDocument(
    override val id: UUID? = null,
    override val paymentId: UUID? = null,
    override val mfaId: UUID? = null,
    override val userId: UUID? = null,
    override val status: PaymentEvaluationDocumentStatus = IN_PROGRESS,
    override val description: String? = null,
    override val amount: InstantSepaPaymentEvaluationDocumentAmount? = null,
    override val creditor: InstantSepaPaymentEvaluationDocumentCreditor? = null,
    override val paymentType: PaymentProcessType? = CREATE,
    var debtor: InstantPyamentEvaluationDeborDocument? = null, // TODO 왜 debtor가 필요하지
    var endToEndId: String? = null,
    val sepaMessageId: String,
    val recurrence: Recurrence? = null,
    val executionOn: Instant? = null
) : PaymentEvaluationDocument {
    // 코틀린 1.5부터 서브 모듈에서 sealed class를 상속받을 수 있다.

    /**
     * how much money is being transferred from the debtor to the creditor
     */
    data class InstantSepaPaymentEvaluationDocumentAmount(
        var value: BigDecimal,
        val currentCode: String
    ): PaymentEvaluationDocumentAmount

    /**
     *
     */
    data class InstantSepaPaymentEvaluationDocumentCreditor(
        var name: String,
        var account: InstantPaymentEvaluationDocumentCreditorAccount? = null,
    ): PaymentEvaluationDocumentCreditor

    data class InstantPaymentEvaluationDocumentCreditorAccount(
        var iban: String? = null, // TODO: 왜 nullable인가?
        var bic: String? = null,
        var verificationOfPayee: VerificationOfPayee? = null // name과 관련된 항목
    ): PaymentEvaluationDocumentAccount

    data class VerificationOfPayee(
        var name: String ?= null,
        var osPlusIdentifier: String ?= null, // 나중에 XOBM에서 verification 할 때 필요
        var result: VerificationOfPayeeResult,
        var correctCreditorName: String ?= null,
        var verificationNotPossibleReason: String? = null
    )

    data class InstantPyamentEvaluationDeborDocument(
        val account: InstantPaymentEvaluationDocumentDebtorAccount,
        val sourceSystem: String
    ): PaymentEvaluationDocumentAmount

    data class InstantPaymentEvaluationDocumentDebtorAccount(
        val id: UUID? = null,
        val currencyCode: String? = null,
        val iban: String? = null,
        val bic: String? = null,
        val accountBalance: BigDecimal? = null,
        val availableBalance: BigDecimal? = null,
        val nearTimeBalance: BigDecimal? = null,
    ): PaymentEvaluationDocumentAccount

    data class Recurrence(
        val from: Instant? = null,
        val until: Instant? = null,
        val frequency: Frequency? = null,
    )

    // @JsonValue 어노테이션은 enum의 값을 JSON으로 직렬화할 때 사용된다.
    enum class Frequency(@JsonValue val value: String) {
        MONTHLY("monthly"),
        EVERY_TWO_MONTHS("bi-monthly"),
        QUARTERLY("quarterly"),
        SEMI_ANNUAL("semi-annually"),
        ANNUAL("annually"),
        ;

        // @JsonCreator 어노테이션은 JSON 문자열을 enum으로 변환할 때 사용된다.
        companion object {
            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): Frequency {
                return entries.firstOrNull { it.value == value }
                    ?: throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }

    enum class VerificationOfPayeeResult(@JsonValue val value: String) {
        MATCH("match"),
        NO_MATCH("no-match"),
        VERIFICATION_NOT_POSSIBLE("verification-not-possible"),
        CLOSE_MATCH("close-match");

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): VerificationOfPayeeResult {
                return entries.firstOrNull { it.value == value }
                    ?: throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }

}

