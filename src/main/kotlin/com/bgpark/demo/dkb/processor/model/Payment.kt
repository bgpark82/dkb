package com.bgpark.demo.dkb.processor.model

import com.bgpark.demo.dkb.processor.dto.OrderCompletedEvent
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Transient
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.domain.AfterDomainEventPublication
import org.springframework.data.domain.DomainEvents
import java.math.BigDecimal

@Entity
class Payment(

    @Id
    @GeneratedValue
    val id: Long,

    val amount: BigDecimal,

    var status: String = "CREATED",

) : AbstractAggregateRoot<Payment>() {

    @Transient
    private val domainEvents = mutableListOf<Any>()

    fun completeOrder() {
        println("complete order event is sent")
        status = "COMPLETED"
        println("domain events : $domainEvents")
        registerEvent(OrderCompletedEvent(id))
        println("complete order event is completed")
    }

    fun registerEvent(event: OrderCompletedEvent) {
        domainEvents.add(OrderCompletedEvent(id))
    }

    /**
     * 도메인 이벤트 발생
     * - save, saveAll
     * - delete, deleteAll, deleteAllInBatch, deleteInBatch
     * - 해당 메소드들을 aggregateRoot를 argument로 받기 때문에, deleteById는 포함되지 않는다
     */
    @DomainEvents
    fun domainEvent(): List<Any> = domainEvents.toList()

    /**
     * 도메인 이벤트 발생 후 호출
     */
    @AfterDomainEventPublication
    fun callbackMethod() {
        // 도메인 이벤트 리스트 초기화
        domainEvents.clear()
    }
}