package com.bgpark.demo.dkb.processor.config

import org.jetbrains.annotations.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@EnableConfigurationProperties(ProcessorConfigurationProperties::class)
class SepaCreditTransferConfiguration

@ConfigurationProperties(prefix = "processor")
@Validated
class ProcessorConfigurationProperties(
    val mfaAuthorized: Properties
) {

    class Properties(
        @field:NotNull
        val enabled: Boolean,
        @field:NotNull
        val batchSize: Int,
        @field:NotNull
        val schedulerDelay: String,
        @field:NotNull
        val nextToBeProcessedAtDelay: String = "PT0S",
        @field:NotNull
        val nextToBeProcessedUntil: String = "PT0S",
    )
}