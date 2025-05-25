package com.bgpark.demo.dkb.processor.controller

import com.bgpark.demo.dkb.processor.dto.OrderCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class OrderEventListener {

    @EventListener
    fun handleOrderCompleted(event: OrderCompletedEvent) : String {
        println("Received order complete event")
        return "Complete order id: ${event.orderId}"
    }
}