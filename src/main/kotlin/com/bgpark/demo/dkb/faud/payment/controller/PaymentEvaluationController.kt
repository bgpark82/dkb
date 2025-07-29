package com.bgpark.demo.dkb.faud.payment.controller

import com.bgpark.demo.dkb.faud.payment.controller.ResourceConstant.Companion.APPLICATION_JSON_API_CONTENT_TYPE
import com.bgpark.demo.dkb.faud.payment.controller.ResourceConstant.Companion.PAYMENT_EVALUATION_PATH
import com.bgpark.demo.dkb.faud.payment.controller.dto.InstantSepaPaymentEvaluationDocument
import com.bgpark.demo.dkb.faud.payment.controller.dto.PaymentEvaluationDocument
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
/**
 * media type: response format of data
 */
@RequestMapping(produces = [APPLICATION_JSON_API_CONTENT_TYPE])
class PaymentEvaluationController {

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping(
//        value = [PAYMENT_EVALUATION_PATH],
//        consumes = [APPLICATION_JSON_API_CONTENT_TYPE]
//    )
//    fun createPaymentEvaluation(
//        @RequestBody resource: PaymentEvaluationDocument
//    ): PaymentEvaluationDocument {
//        return InstantSepaPaymentEvaluationDocument.InstantPyamentEvaluationDeborDocument()
////        return when (resource) {
//////            is InstantSepaPaymentEvaluationDocument -> resource.instantSepaCreditTransferCaseData()
////        }.let {
////
////        }
//    }
}