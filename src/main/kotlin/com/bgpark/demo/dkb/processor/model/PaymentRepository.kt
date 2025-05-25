package com.bgpark.demo.dkb.processor.model

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
}