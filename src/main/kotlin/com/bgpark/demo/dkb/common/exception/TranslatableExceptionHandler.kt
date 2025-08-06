package com.bgpark.demo.dkb.common.exception

import com.bgpark.demo.dkb.common.i18n.MessageKey
import com.bgpark.demo.dkb.common.i18n.MessageKeys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class TranslatableExceptionHandler(
    private val messageSource: MessageSource,
) {
    val logger = LoggerFactory.getLogger(TranslatableExceptionHandler::class.java)

    @ExceptionHandler(TranslatableException::class)
    fun handleIllegalArgumentException(
        ex: TranslatableException,
        request: HttpServletRequest,
    ): ResponseEntity<String> {
        logger.info("Handling TranslatableException: ${ex.message}", request, ex)
        val code = MessageKeys.ExceptionKeys.entries.find { it.code == ex.code } ?: MessageKeys.ExceptionKeys.GENERIC_ERROR_MESSAGE
        val locale = LocaleContextHolder.getLocale()
        val errorMessage = messageSource.getMessage(code.code, null, locale)
        return ResponseEntity.status(ex.status).body(errorMessage)
    }

}