package com.bgpark.demo.dkb.faud.payment.controller

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.OffsetDateTime

@RestController
class TestController {

    @PostMapping("/receive")
    fun receive(@RequestBody request: TestRequest): String {
        println(request)
        println(request.createdAt)
        println(request.updatedAt)
        println("datetime string: ${request.createdAtString}")
        println("datetime string: ${request.updatedAtString}")
        println(request.createdAtInstant)
        println(request.updatedAtInstant)
        return "Received at: ${request.createdAtString}"
    }

    @PostMapping("/tests")
    fun createTest(): String {
        val restTemplate = org.springframework.web.client.RestTemplate()
        val request = TestRequestV1(
            createdAt = OffsetDateTime.now(),
            updatedAt = Instant.now(),
            createdAtString = OffsetDateTime.now(),
            updatedAtString = Instant.now(),
            createdAtInstant = OffsetDateTime.now(),
            updatedAtInstant = Instant.now(),
        )
        val response = restTemplate.postForObject(
            "http://localhost:8080/receive",
            request,
            String::class.java
        )
        return response ?: "No response"
    }

    /**
     * Instant로 보내면 epoch time으로 변환됨
     * OffsetDateTime으로 보내면 ISO 8601 형식으로 변환됨
     *
     */
    data class TestRequestV1(
        val createdAt: OffsetDateTime,

        val updatedAt: Instant,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        val createdAtString: OffsetDateTime,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        val updatedAtString: Instant,

        val createdAtInstant: OffsetDateTime,

        val updatedAtInstant: Instant,

        val message: String = "Hello, World!"
    )

    data class TestRequest(
        val createdAt: String,
        val updatedAt: String,
        val createdAtString: String,
        val updatedAtString: String,
        val createdAtInstant: Instant,
        val updatedAtInstant: Instant,
    )
}