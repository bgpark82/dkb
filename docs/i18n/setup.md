# Spring Boot 다국어(i18n) 오류 메시지 지원 설정

Spring Boot 애플리케이션에서 발생하는 예외(Exception)에 대해 다국어 오류 메시지를 제공하는 방법을 설명합니다. 이를 통해 클라이언트의 요청 언어(예: `Accept-Language` 헤더)에 따라 적절한 언어로 오류 메시지를 응답할 수 있습니다.

## 1. 메시지 프로퍼티 파일 생성

먼저, 언어별 메시지를 담을 프로퍼티 파일을 `src/main/resources` 경로에 생성합니다.

-   **기본 메시지 파일 (messages.properties)**: 특정 언어 설정이 없을 때 사용될 기본 메시지입니다.
-   **한국어 메시지 파일 (messages_ko.properties)**: 한국어 메시지입니다.
-   **영어 메시지 파일 (messages_en.properties)**: 영어 메시지입니다.

**파일 구조:**
```
src/
└── main/
    └── resources/
        ├── messages.properties
        ├── messages_ko.properties
        └── messages_en.properties
```

**파일 내용 예시:**

`messages.properties` (기본값: 영어)
```properties
error.user.notFound=User not found.
error.invalid.input=Invalid input value.
```

`messages_ko.properties`
```properties
error.user.notFound=사용자를 찾을 수 없습니다.
error.invalid.input=잘못된 입력 값입니다.
```

`messages_en.properties`
```properties
error.user.notFound=User not found.
error.invalid.input=Invalid input value.
```

## 2. MessageSource 설정

Spring Boot는 `src/main/resources`에 `messages.properties` 파일이 있으면 `MessageSource`를 자동으로 구성해 줍니다. 대부분의 경우 이 자동 설정을 사용하는 것이 편리합니다.

`application.yml`에서 관련 설정을 추가할 수 있습니다.

`application.yml`
```yaml
spring:
  messages:
    basename: messages # 메시지 파일들의 기본 이름을 지정
    encoding: UTF-8      # 메시지 파일 인코딩
    always-use-message-format: false # 메시지 포맷팅 항상 사용 여부
    fallback-to-system-locale: true # 시스템 로케일로 폴백 여부
```
-   `basename`: 메시지 파일들의 기본 이름(`messages`)을 지정합니다. Spring은 이 이름을 기반으로 `messages_ko`, `messages_en` 등의 파일을 찾습니다. 쉼표로 구분하여 여러 기본 이름을 지정할 수도 있습니다. (예: `messages,errors`)
-   `encoding`: 메시지 파일의 인코딩을 UTF-8로 설정하여 한글 깨짐을 방지합니다.

### MessageSource 직접 설정 (수동 구성)

만약 메시지 파일이 클래스패스 외부(`src/main/resources`가 아닌 다른 경로)에 있거나, 더 세밀한 설정이 필요하다면 `MessageSource` Bean을 직접 정의할 수 있습니다.

`@Configuration` 클래스를 생성하여 `MessageSource` Bean을 등록합니다.

```kotlin
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

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
        return messageSource
    }
}
```

**코드 설명:**

-   `ReloadableResourceBundleMessageSource`: 애플리케이션을 재시작하지 않고도 메시지 프로퍼티 파일의 변경사항을 반영할 수 있는 `MessageSource` 구현체입니다.
-   `setBasename("classpath:/messages")`: `src/main/resources` 폴더 아래에 있는 `messages`로 시작하는 파일들을 메시지 소스로 사용하겠다고 명시적으로 지정합니다.
-   `setCacheSeconds(10)`: 프로퍼티 파일을 10초 동안 캐싱합니다. 운영 환경에서는 성능을 위해 캐시 시간을 적절히 설정하는 것이 좋습니다.

> **주의**: `MessageSource` Bean을 직접 등록하면 Spring Boot의 자동 설정은 비활성화됩니다. 따라서 `spring.messages` 관련 `application.yml` 설정은 적용되지 않으므로, 필요한 모든 설정을 Bean 설정 코드에 직접 작성해야 합니다.

## 3. 전역 예외 핸들러(Global Exception Handler) 구현

`@RestControllerAdvice`를 사용하여 애플리케이션 전역에서 발생하는 예외를 처리하고, `MessageSource`를 이용해 다국어 메시지를 반환합니다.

```kotlin
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// 예시를 위한 커스텀 예외
class UserNotFoundException : RuntimeException()

// 예시를 위한 에러 응답 DTO
data class ErrorResponse(val message: String)

@RestControllerAdvice
class GlobalExceptionHandler(private val messageSource: MessageSource) {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        // 현재 요청의 Locale 정보를 가져옴
        val locale = LocaleContextHolder.getLocale()

        // Locale에 맞는 메시지를 프로퍼티 파일에서 조회
        val errorMessage = messageSource.getMessage("error.user.notFound", null, locale)

        val response = ErrorResponse(errorMessage)
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    // 다른 예외 핸들러들...
}
```

**코드 설명:**

1.  `@RestControllerAdvice`: 모든 `@RestController`에서 발생하는 예외를 이 클래스에서 처리하도록 지정합니다.
2.  `MessageSource` 주입: 생성자를 통해 `MessageSource` Bean을 주입받습니다.
3.  `@ExceptionHandler`: 특정 예외(`UserNotFoundException`)가 발생했을 때 `handleUserNotFoundException` 메서드가 실행되도록 합니다.
4.  `LocaleContextHolder.getLocale()`: Spring이 HTTP 요청의 `Accept-Language` 헤더를 분석하여 설정해준 현재 `Locale` 정보를 가져옵니다.
5.  `messageSource.getMessage(code, args, locale)`: 
    -   `code`: 메시지 프로퍼티 파일의 키 (`error.user.notFound`)
    -   `args`: 메시지에 동적인 값을 추가할 때 사용 (예: `사용자 {0}을(를) 찾을 수 없습니다.` 에서 `{0}`에 들어갈 값). 여기서는 `null`입니다.
    -   `locale`: 조회할 언어. 이 `Locale`에 따라 적절한 `messages_xx.properties` 파일이 선택됩니다.

## 4. 테스트

API를 호출할 때 `Accept-Language` 헤더 값을 다르게 설정하여 테스트할 수 있습니다.

**한국어 메시지 요청:**
```bash
curl -X GET http://localhost:8080/api/users/123 \
     -H "Accept-Language: ko-KR"
```
**응답:**
```json
{
  "message": "사용자를 찾을 수 없습니다."
}
```

**영어 메시지 요청:**
```bash
curl -X GET http://localhost:8080/api/users/123 \
     -H "Accept-Language: en-US"
```
**응답:**
```json
{
  "message": "User not found."
}
```
