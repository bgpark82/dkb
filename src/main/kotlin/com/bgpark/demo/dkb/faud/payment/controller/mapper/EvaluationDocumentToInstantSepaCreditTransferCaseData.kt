package com.bgpark.demo.dkb.faud.payment.controller.mapper

import com.bgpark.demo.dkb.faud.payment.controller.dto.InstantSepaPaymentEvaluationDocument
import com.bgpark.demo.dkb.faud.payment.controller.dto.PaymentEvaluationDocument
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.function.Supplier

@Component
class EvaluationDocumentToInstantSepaCreditTransferCaseData
//        :(PaymentEvaluationDocument) -> InstantSepaPaymentEvaluationDocument {

//    override fun invoke(input: PaymentEvaluationDocument): InstantSepaPaymentEvaluationDocument {
//        return InstantSepaPaymentEvaluationDocument(
//            // TODO: com.fasterxml.uuid.Generators.timeBasedEpochRandomGenerator()를 사용
//            id = UUID.randomUUID(),
//            paymentId = paymentId,
//            mfaId = UUID.randomUUID(),
//            userId = UUID.randomUUID(),
//
//        )
//    }
//}