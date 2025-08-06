package com.bgpark.demo.dkb.common.i18n

import org.slf4j.LoggerFactory

object MessageKeys {

    val logger = LoggerFactory.getLogger(javaClass)

    enum class ExceptionKeys(
        override val key: String,
    ) : MessageKey {
        FRAUD_REJECTED("exception.fraud.rejected"),
        GENERIC_ERROR_MESSAGE("exception.sepaCreditTransfer.genericErrorMessage"),
        ;

    }
}