package com.bgpark.demo.dkb.processor.domain

import com.bgpark.demo.dkb.processor.model.PaymentStatus
import com.bgpark.demo.dkb.processor.model.PaymentType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Payment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    var paymentStatus: PaymentStatus?,

    @Enumerated(EnumType.STRING)
    val paymentType: PaymentType?,

    val isInstantPayment: Boolean? = null,

    val updatedAt: LocalDateTime? = LocalDateTime.now()
) {
}