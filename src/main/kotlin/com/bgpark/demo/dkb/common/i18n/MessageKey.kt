package com.bgpark.demo.dkb.common.i18n

interface MessageKey {

    val key: String
    val code: String
        get(): String = key
    val title: String
        get() = "$key.title"
    val detail: String
        get() = "$key.detail"
}