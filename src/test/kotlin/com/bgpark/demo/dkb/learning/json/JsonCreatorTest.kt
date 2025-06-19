package com.bgpark.demo.dkb.learning.json

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JsonCreatorTest {

    @Test
    fun `given string when it is deserialized without JsonCreator then throw exception`() {
        // given
        val jsonString = "\"no-match\""

        // when & then
        assertThrows<InvalidFormatException> {
            ObjectMapper().readValue<VerificationOfPayeeV1>(jsonString)
        }
    }

    private enum class VerificationOfPayeeV1(
        @JsonValue val value: String
    ) {
        NO_MATCH("no-match");
    }

    @Test
    fun `given string when it is deserialized with JsonCreator then enum`() {
        // given
        val jsonString = "\"no-match\""
        val expectedValue = VerificationOfPayeeV2.NO_MATCH

        // when
        val actualValue = ObjectMapper().readValue<VerificationOfPayeeV2>(jsonString)

        // then
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    private enum class VerificationOfPayeeV2(
        @JsonValue val value: String
    ) {
        NO_MATCH("no-match");

        companion object {
            private val VALUES = entries.associateBy { it.value }

            @JsonCreator
            fun fromValue(value: String): VerificationOfPayeeV2 {
                return VALUES[value] ?: throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}