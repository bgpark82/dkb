package com.bgpark.demo.dkb.faud.payment.service.model

import com.bgpark.demo.dkb.faud.payment.service.model.ResourceConstants.InstantSepaCreditTransferEvaluation
import com.bgpark.demo.dkb.faud.payment.service.model.TransferMechanism.SWIFT
import com.bgpark.demo.dkb.faud.payment.service.model.creditor.CreditorAccountWithIban
import com.bgpark.demo.dkb.faud.payment.service.model.debtor.Account
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.UUID

data class InstantSepaCreditTransferCaseData(
    override val id: UUID,
    override val paymentId: UUID,
    override val mfaId: UUID,
    override var userId: UUID?,
    override val description: String,
    override val amountValue: BigDecimal,
    override val amountCurrencyCode: String,
    override val creditorAccount: CreditorAccountWithIban,
    override val creditorName: String,
    override val transferMechanism: TransferMechanism = SWIFT,
    override var authorizedAt: String? = null,
    override var challengeValidationAttempts: Int? = null,
    override var obvId: String? = null,
    override var persNr: String? = null,
    override var createdAt: ZonedDateTime,
    override var sessionId: UUID? = null,
    override var executionDate: String? = null,
    override var initiationReason: InitiationReason,
    override var sourceSystem: SourceSystem = SourceSystem.NOVA,
    override var deviceData: DeviceData = DeviceData(),
    override var processType: ProcessType? = null,
    override var webDeviceData: WebDeviceData? = null,
    override var mfaChallengeMethodType: MfaChallengeMethodType? = null,
    override var paymentType: PaymentType? = null,
    override var mfaPortfolio: Portfolio? = null,
    override var mfaStatus: MfaStatus? = null,
    override var mfaExpires: OffsetDateTime? = null,
    override var mfaMethodId: UUID? = null,
    override var mfaType: String? = null,
    override var obvTnId: String? = null,
    override var personenNr: String? = null,
    override var personenNrUnpadded: String? = null,
    override var requestType: String? = InstantSepaCreditTransferEvaluation.TYPE,
    override val verificationOfPayee: VerificationOfPayee? = null,
    val debtorAccount: Account,
    val endToEndId: String? = null,
    val sepaMessageId: String,
    val from: Instant? = null,
    val until: Instant? = null,
    val executionOn: Instant? = null,
//    val xs2a: Xs2aData? = null,
): CaseData