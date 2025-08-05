package com.bgpark.demo.dkb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
class DkbApplication

fun main(args: Array<String>) {
    runApplication<DkbApplication>(*args)
}
