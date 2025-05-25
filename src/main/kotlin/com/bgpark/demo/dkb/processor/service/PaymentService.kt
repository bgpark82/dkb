package com.bgpark.demo.dkb.processor.service

import com.bgpark.demo.dkb.processor.model.Payment
import com.bgpark.demo.dkb.processor.model.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {

    @Transactional
    fun save(paymentId: Long) =
        Payment(id = paymentId, amount = BigDecimal("1000")).run {
            this.completeOrder()
            paymentRepository.save(this)
    }
}