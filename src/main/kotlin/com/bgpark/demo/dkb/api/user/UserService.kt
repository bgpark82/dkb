package com.bgpark.demo.dkb.api.user

import org.springframework.stereotype.Service

@Service
class UserService {

    private val validUsers = mapOf(
        "john.doe@example.com" to "password123",
        "jane.smith@example.com" to "securepass"
    )

    fun authenticate(email: String, password: String): User? =
        validUsers.hasEmail(email)
            ?.isSamePassword(password)
            ?.createUser(email, password)

    fun Map<String, String>.hasEmail(email: String): String? = validUsers[email]

    fun String.isSamePassword(password: String): String? = takeIf { it == password }

    fun String.createUser(email: String, password: String): User? = let { User(email, password) }
}