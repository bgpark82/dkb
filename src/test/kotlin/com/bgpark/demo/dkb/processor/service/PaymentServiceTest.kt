package com.bgpark.demo.dkb.processor.service

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class PaymentServiceTest(
    @Autowired private val paymentService: PaymentService,
) {

    @Test
    @Transactional
    fun `결제를 완료하면 OrderCompletedEvent가 발생한다`() {
        // given & when
        val savedPayment = paymentService.save(1L)

        // then
        assertThat(savedPayment.status).isEqualTo("COMPLETED")
    }
}
