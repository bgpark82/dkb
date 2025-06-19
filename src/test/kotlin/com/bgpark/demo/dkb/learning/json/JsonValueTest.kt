package com.bgpark.demo.dkb.learning.json

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @JsonValue
 */
class JsonValueTest {

    @Test
    fun `given string when string is deserialized without JsonValue annotation then return same name as enum`() {
        // given
        val expectedJson = "\"NO_MATCH\""

        // when
        val actualJson = ObjectMapper().writeValueAsString(VerificationOfPayeeV1.NO_MATCH)

        // then
        assertThat(actualJson).isEqualTo(expectedJson)
    }

    private enum class VerificationOfPayeeV1(
        val value: String
    ) {
        MATCH("match"),
        NO_MATCH("no-match"),
    }

    @Test
    fun `given string when string is deserialized with JsonValue annotation then return actual name`() {
        // given
        val expectedJson = "\"no-match\""

        // when
        val actualJson = ObjectMapper().writeValueAsString(VerificationOfPayeeV2.NO_MATCH)

        // then
        assertThat(actualJson).isEqualTo(expectedJson)
    }

    private  enum class VerificationOfPayeeV2(
        @JsonValue val value: String
    ) {
        MATCH("match"),
        NO_MATCH("no-match"),
    }
}