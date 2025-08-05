package com.bgpark.demo.dkb.processor.domain

import com.bgpark.demo.dkb.processor.model.PaymentStatus
import com.bgpark.demo.dkb.processor.model.PaymentType
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.time.LocalDateTime

interface PaymentRepository : JpaRepository<Payment, Long>{

    @Transactional
    @PessimisticWriteLockWithSkipLocked
    @Query("""
        SELECT p
        FROM Payment p
        WHERE p.paymentStatus = :status
          AND p.paymentType = :type
          AND p.isInstantPayment IS TRUE
          AND p.updatedAt < :updatedAt
        ORDER BY p.updatedAt ASC
        LIMIT 1
    """)
    fun findNextFromInstantPaymentsByStatusAndPaymentType(
        @Param("status") paymentStatus: PaymentStatus,
        @Param("type") paymentType: PaymentType,
        @Param("updatedAt") updatedAt: LocalDateTime = LocalDateTime.now()
    ): Payment?
}