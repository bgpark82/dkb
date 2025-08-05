# MDC의 모든 함수 설명 (Kotlin 예제)

`org.slf4j.MDC` 클래스는 스레드 로컬(thread-local) 저장소를 사용하여 로그에 컨텍스트 정보를 추가하는 여러 정적(static) 함수를 제공합니다. 각 함수의 역할과 Kotlin 사용 예제는 다음과 같습니다.

### 1. `put(key: String, value: String)`

현재 스레드의 MDC(Mapped Diagnostic Context) 맵에 키-값 쌍을 추가합니다. 만약 동일한 키가 이미 존재한다면, 기존 값은 새로 입력된 값으로 덮어쓰여집니다.

**주요 용도:** 로그에 추적하고 싶은 정보(예: 요청 ID, 사용자 ID)를 설정할 때 사용합니다.

```kotlin
import org.slf4j.MDC
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("MdcExample")

fun processRequest(requestId: String, userId: String) {
    // MDC에 컨텍스트 정보 추가
    MDC.put("requestId", requestId)
    MDC.put("userId", userId)

    logger.info("사용자 요청 처리 시작") // 이 로그에는 requestId와 userId가 포함됨

    // ... 비즈니스 로직 ...

    logger.info("사용자 요청 처리 완료")

    // 요청 처리가 끝나면 반드시 clear()를 호출해야 함
    MDC.clear()
}
```

### 2. `putCloseable(key: String, value: String): MDC.MDCCloseable`

`put`과 유사하게 MDC에 키-값 쌍을 추가하지만, `AutoCloseable` 인터페이스를 구현한 `MDCCloseable` 객체를 반환합니다. 이 기능은 Kotlin의 `use` 블록(Java의 try-with-resources)과 함께 사용될 때 매우 유용하며, 블록을 벗어날 때 자동으로 해당 키를 MDC에서 제거해 줍니다. `clear()` 호출을 잊어버리는 실수를 방지하는 데 도움이 됩니다.

**주요 용도:** 특정 코드 블록 내에서만 유효한 MDC 값을 설정하고, 블록이 끝나면 자동으로 정리하고 싶을 때 사용합니다.

```kotlin
import org.slf4j.MDC
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("MdcExample")

fun processWithCloseable(requestId: String) {
    MDC.putCloseable("requestId", requestId).use {
        logger.info("요청 처리 중...") // 이 로그에는 requestId가 포함됨
        // ... 로직 ...
    } // 이 블록을 나가면 requestId가 자동으로 MDC에서 제거됨

    logger.info("요청 처리 완료.") // 이 로그에는 더 이상 requestId가 포함되지 않음
}
```

### 3. `get(key: String): String?`

현재 스레드의 MDC 맵에서 주어진 키에 해당하는 값을 가져옵니다. 만약 키가 존재하지 않으면 `null`을 반환합니다.

**주요 용도:** MDC에 설정된 값을 코드 내에서 직접 확인하거나 사용해야 할 때 쓰입니다.

```kotlin
import org.slf4j.MDC

fun checkRequestId() {
    val requestId = MDC.get("requestId")
    if (requestId != null) {
        println("현재 요청 ID는 $requestId 입니다.")
    } else {
        println("요청 ID가 설정되지 않았습니다.")
    }
}
```

### 4. `remove(key: String)`

현재 스레드의 MDC 맵에서 특정 키-값 쌍을 제거합니다. 

**주요 용도:** 여러 MDC 값 중 특정 값만 제거하고 싶을 때 사용합니다. `putCloseable`을 사용하면 이 함수를 직접 호출할 필요가 줄어듭니다.

```kotlin
import org.slf4j.MDC
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("MdcExample")

fun someLogic() {
    MDC.put("requestId", "req-123")
    MDC.put("tempData", "temporary-value")

    logger.info("임시 데이터와 함께 로깅")

    // 임시 데이터만 MDC에서 제거
    MDC.remove("tempData")

    logger.info("임시 데이터 제거 후 로깅") // 이 로그에는 더 이상 tempData가 없음

    MDC.clear()
}
```

### 5. `clear()`

현재 스레드의 MDC 맵에 있는 **모든** 키-값 쌍을 제거합니다.

**주요 용도:** 스레드 풀 환경에서 스레드가 재사용될 때 이전 요청의 MDC 정보가 다음 요청에 영향을 주지 않도록, 요청 처리가 완료되는 시점(예: 서블릿 필터의 `finally` 블록)에 반드시 호출해야 합니다.

```kotlin
import org.slf4j.MDC

fun handleRequest() {
    try {
        MDC.put("requestId", "req-456")
        // ... 로직 처리 ...
    } finally {
        // 어떤 경우에도 요청 처리가 끝나면 MDC를 비워줌
        MDC.clear()
    }
}
```

### 6. `getCopyOfContextMap(): Map<String, String>?`

현재 스레드의 MDC 맵 전체를 복사하여 `Map` 형태로 반환합니다. 반환된 맵은 원본 MDC 맵과 독립적이므로, 이 맵을 수정해도 실제 MDC에는 영향을 주지 않습니다.

**주요 용도:** 부모 스레드의 MDC 컨텍스트를 자식 스레드로 전달하는 등 비동기 처리나 고급 시나리오에서 사용됩니다.

> **참고:** `MDC.getContext()`라는 오래된 함수도 있지만, 이 함수는 SLF4J 1.5.1부터 deprecated 되었으며 `getCopyOfContextMap()`과 동일하게 동작합니다. 항상 `getCopyOfContextMap()`을 사용하는 것이 좋습니다.

```kotlin
import org.slf4j.MDC
import kotlin.concurrent.thread

fun parentThreadLogic() {
    MDC.put("requestId", "req-789")
    val parentContext: Map<String, String>? = MDC.getCopyOfContextMap()

    thread {
        // 자식 스레드에서 부모의 MDC 컨텍스트를 설정
        if (parentContext != null) {
            MDC.setContextMap(parentContext)
        }
        // 이제 이 스레드의 로그에는 부모의 requestId가 포함됨
        // ... 자식 스레드 로직 ...
        MDC.clear()
    }
}
```

### 7. `setContextMap(contextMap: Map<String, String>)`

주어진 `Map`으로 현재 스레드의 MDC 맵을 완전히 교체합니다. 기존에 MDC에 있던 값들은 모두 사라지고 `contextMap`의 내용으로 채워집니다.

**주요 용도:** `getCopyOfContextMap()`과 함께 사용하여 다른 스레드로부터 MDC 컨텍스트를 복원할 때 사용됩니다. (위 `getCopyOfContextMap()` 예제 참고)

### 8. `getMDCAdapter(): MDCAdapter`

SLF4J의 내부 구현체인 `MDCAdapter`를 반환합니다. `MDCAdapter`는 실제 MDC 기능을 수행하는 인터페이스입니다. (예: Logback의 `LogbackMDCAdapter`)

**주요 용도:** 일반적인 애플리케이션 개발에서는 거의 사용되지 않습니다. SLF4J와 연동되는 프레임워크를 개발하거나 매우 특수한 로깅 처리가 필요할 때 제한적으로 사용될 수 있습니다.