package com.bgpark.demo.dkb.processor.domain

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>{

}