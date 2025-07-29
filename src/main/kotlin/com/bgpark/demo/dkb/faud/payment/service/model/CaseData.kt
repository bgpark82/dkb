package com.bgpark.demo.dkb.faud.payment.service.model

import com.bgpark.demo.dkb.faud.payment.service.model.creditor.CreditorAccount
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.UUID

sealed interface CaseData {
    val id: UUID
    val paymentId: UUID
    val mfaId: UUID
    val description: String

    // amount
    val amountValue: BigDecimal
    val amountCurrencyCode: String?

    // creditor
    val creditorName: String
    val creditorAccount: CreditorAccount

    val transferMechanism: TransferMechanism
    var challengeValidationAttempts: Int?
    var obvId: String?
    var persNr: String?
    var sessionId: UUID?
    var initiationReason: InitiationReason
    var sourceSystem: SourceSystem
    var deviceData: DeviceData
    var processType: ProcessType?
    var webDeviceData: WebDeviceData?
    var userId: UUID?
    var paymentType: PaymentType?
    var obvTnId: String?
    var requestType: String?
    val verificationOfPayee: VerificationOfPayee?

    // person
    var personenNr: String?
    var personenNrUnpadded: String?

    // time
    var authorizedAt: String?
    var createdAt: ZonedDateTime
    var executionDate: String?

    // mfa
    var mfaChallengeMethodType: MfaChallengeMethodType?
    var mfaPortfolio: Portfolio? //
    var mfaStatus: MfaStatus?
    var mfaExpires: OffsetDateTime?
    var mfaMethodId: UUID?
    var mfaType: String?

}