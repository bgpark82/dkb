package com.bgpark.demo.dkb.faud.payment.controller.dto

import java.util.UUID

/**
 * sealed interface
 * 1. 컴파일 타임에 when 구문에서 하위 타입 검사 가능
 * 2. 해당 인터페이스를 다른 라이브러리에서 구현 불가능, 라이브러리를 만들 때 유용
 * 3. 다중 인터페이스 구현 가능
 */
sealed interface PaymentEvaluationDocument {

    val id: UUID?

    val status: PaymentEvaluationDocumentStatus

    val paymentId: UUID?

    val mfaId: UUID?

    val userId: UUID?

    val description: String?

    val amount: PaymentEvaluationDocumentAmount?

    val creditor: PaymentEvaluationDocumentCreditor?

    val paymentType: PaymentProcessType?

}
