package com.bgpark.demo.dkb.common.exception

import org.springframework.http.HttpStatus

open class TranslatableException(
    val status: HttpStatus,
    val code: String,
    val title: String,
    val detail: String = "",
    val meta: Map<String, String>? = null,
    override val cause: Throwable? = null,
) : RuntimeException("[$status] $code", cause) {
}