package com.bgpark.demo.dkb.processor

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
// 조건이 맞으면 bean 생성, application.yaml에 따라 결정
@ConditionalOnProperty(
    value = ["processor.mfa_authorized.enabled"], // 해당 프로퍼티가 true일 때만 활성화
    havingValue = "true", // 해당 값과 일치할 때만 활성화
    matchIfMissing = true // 프로퍼티가 없을 때도 활성화
)
class SepaCreditTransferProcessor {
    init {
        println("${SepaCreditTransferProcessor::class.java.simpleName} created!")
    }

    @Scheduled(
        // 이전 작업 시작 후 지정된 시간 간격으로 다음 작업을 시작,
        // 만약 작업이 7초가 걸리면, 5초가 지정되어 있어도 다음 작업은 7초 후에 시작
        fixedRateString = "\${processor.mfa_authorized.fixedRate}",
        // 이전 작업이 끝난 후 지정된 시간만큼 기다린 후 다음 작업을 시작
        // 만약 작업이 7초 걸리면, 5초 후에 다음 작업 시작
        // fixedDelayString = "\${processor.mfa_authorized.fixedDelay}",
        initialDelay = 1000L, // 초기 지연 시간 (1초)
    )
    fun processMfaAuthorizedSepaInstant() {
        println("Processing MFA authorized SEPA instant payments...")
        // 여기에 실제 처리 로직을 구현
        Thread.sleep(10000)
        println("작업 완료 중")
    }
}