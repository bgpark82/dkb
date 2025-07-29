package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.model.PaymentV1
import com.bgpark.demo.dkb.processor.model.PaymentRepositoryV1
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepositoryV1: PaymentRepositoryV1
) {

    @Transactional
    fun save(paymentId: Long) =
        PaymentV1(id = paymentId, amount = BigDecimal("1000")).run {
            this.completeOrder()
            paymentRepositoryV1.save(this)
    }
}