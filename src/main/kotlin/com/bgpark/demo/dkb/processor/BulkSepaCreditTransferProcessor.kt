package com.bgpark.demo.dkb.processor

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class BulkSepaCreditTransferProcessor {

    @Scheduled(
        fixedRateString = "1000"
    )
    fun processBulkSepaCreditTransfer() {
        println("hello")
    }
}