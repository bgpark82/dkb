package com.bgpark.demo.dkb.common.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.Locale

@Configuration
class MessageSourceConfig : WebMvcConfigurer {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        // 메시지 파일의 경로와 기본 이름을 지정합니다.
        // 'classpath:'는 'src/main/resources'를 의미합니다.
        messageSource.setBasename("classpath:/messages")
        // 기본 인코딩을 UTF-8로 설정합니다.
        messageSource.setDefaultEncoding("UTF-8")
        // 캐시 시간(초)을 설정합니다. -1은 캐시를 사용하지 않겠다는 의미로, 개발 중에는 유용합니다.
        messageSource.setCacheSeconds(10)
        // 해당 코드에 대한 메시지를 찾지 못했을 경우, 시스템 로케일로 다시 찾을지 여부를 결정합니다.
        messageSource.setFallbackToSystemLocale(false)
        messageSource.setDefaultLocale(Locale.ENGLISH)
        return messageSource
    }
}