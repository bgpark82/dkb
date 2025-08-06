package com.bgpark.demo.dkb.common.exception

import com.bgpark.demo.dkb.common.i18n.MessageKey
import org.springframework.http.HttpStatus

abstract class SimpleTranslatableException(
    val status: HttpStatus,
    val key: MessageKey,
    val arguments: List<String> = emptyList(),
) {


}