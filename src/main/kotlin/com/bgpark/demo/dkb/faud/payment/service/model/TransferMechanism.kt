package com.bgpark.demo.dkb.faud.payment.service.model

enum class TransferMechanism {
    /**
     * Society for Worldwide Interbank Financial Telecommunication
     * - 전 세계 은행 간 금융 메세지를 주고 받는 메세징 네트워크 프로토콜 (금융 우체국)
     */
    SWIFT,
    /**
     * Single Euro Payments Area
     * - 유럽 연합 국가 간의 유로화 결제 시스템
     */
    SEPA_NATIONAL,

    /**
     * Single Euro Payments Area International
     * - 유럽 연합 국가 외의 국가 간의 유로화 결제 시스템
     */
    SEPA_INTERNATIONAL;
}