package com.bgpark.demo.dkb.processor.model

import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepositoryV1 : JpaRepository<PaymentV1, Long> {
}