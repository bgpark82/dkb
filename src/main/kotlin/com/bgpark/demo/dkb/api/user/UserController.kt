package com.bgpark.demo.dkb.api.user

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping("/api/login")
    fun apiLogin(
        @RequestBody loginRequest: Map<String, String>
    ): Map<String, Any> {
        val email = loginRequest["username"] ?: ""
        val password = loginRequest["password"] ?: ""

        val user = userService.authenticate(email, password)
        return if (user != null) {
            mapOf("success" to true, "message" to "Login successful!", "user" to user)
        } else {
            mapOf("success" to false, "message" to "Invalid credentials.")
        }
    }
}