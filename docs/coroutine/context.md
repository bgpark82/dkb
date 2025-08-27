### 1\. 로그 메시지에 `MDCContext` 정보 적용하기

`MDCContext`는 코루틴 내에서 발생하는 모든 로그 메시지에 특정 컨텍스트 정보를 자동으로 추가합니다. 이 예시에서는 요청 ID(`requestId`)를 MDC에 저장하여 로그에 포함하는 방법을 보여줍니다.

```kotlin
import kotlinx.coroutines.*
import org.slf4j.MDC
import kotlin.random.Random

// MDC 정보를 코루틴 컨텍스트에 담기 위한 클래스 (log4j나 logback에서 사용 가능)
// MDCContext는 kotlinx.coroutines.slf4j.MDCContext에 포함되어 있음
import kotlinx.coroutines.slf4j.MDCContext

fun main() = runBlocking {
    // 요청 ID를 MDC에 저장
    val requestId = "req-${Random.nextInt(1000)}"
    MDC.put("requestId", requestId)
    
    // MDCContext를 코루틴에 적용
    launch(MDCContext()) {
        logMessage("첫 번째 작업 시작")
        delay(500)
        logMessage("첫 번째 작업 완료")
    }
    
    launch(MDCContext()) {
        logMessage("두 번째 작업 시작")
        delay(200)
        logMessage("두 번째 작업 완료")
    }

    delay(1000)
    // MDC 정보 정리
    MDC.clear()
}

// SLF4J 로거를 사용하여 로그 메시지 출력
fun logMessage(message: String) {
    println("[requestId: ${MDC.get("requestId")}] - $message")
}
```

**실행 결과**:

```
[requestId: req-XXX] - 첫 번째 작업 시작
[requestId: req-XXX] - 두 번째 작업 시작
[requestId: req-XXX] - 두 번째 작업 완료
[requestId: req-XXX] - 첫 번째 작업 완료
```

이 예시에서는 `MDC.put`으로 설정한 `requestId`가 `launch(MDCContext())`를 통해 시작된 두 코루틴의 로그에 모두 자동으로 포함됩니다. 만약 `MDCContext()`를 사용하지 않았다면, 로그에 `requestId` 정보가 제대로 나타나지 않았을 수 있습니다.

-----

### 2\. 스레드 전환 시 `MDCContext`의 동작

`withContext`를 사용해 스레드가 전환될 때 `MDCContext`가 어떻게 로그 컨텍스트를 유지하는지 보여주는 예시입니다.

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.MDC
import kotlin.random.Random

fun main() = runBlocking {
    val userId = "user-${Random.nextInt(100)}"
    
    // 코루틴 시작 시 MDC에 userId를 저장
    MDC.put("userId", userId)
    
    launch(MDCContext()) {
        // 현재 스레드 정보와 함께 로그 출력
        println("[${Thread.currentThread().name}] - [userId: ${MDC.get("userId")}] 작업 시작")
        
        // withContext를 사용해 I/O 스레드로 전환
        withContext(Dispatchers.IO) {
            println("[${Thread.currentThread().name}] - [userId: ${MDC.get("userId")}] I/O 작업 중")
            delay(500)
        }
        
        // 작업이 다시 main 스레드로 돌아와도 MDC 정보가 유지됨
        println("[${Thread.currentThread().name}] - [userId: ${MDC.get("userId")}] 작업 완료")
    }
    
    delay(1000)
    MDC.clear()
}
```

**실행 결과**:

```
[main @coroutine#2] - [userId: user-YYY] 작업 시작
[DefaultDispatcher-worker-1 @coroutine#2] - [userId: user-YYY] I/O 작업 중
[main @coroutine#2] - [userId: user-YYY] 작업 완료
```

이 예시에서 코루틴은 `main` 스레드에서 시작해 `withContext(Dispatchers.IO)`를 통해 다른 스레드(`DefaultDispatcher-worker`)로 전환됩니다. 하지만 로그 메시지를 보면 **`userId` 정보는 스레드가 바뀌어도 그대로 유지**되는 것을 볼 수 있습니다. `MDCContext` 덕분에 개발자가 별도로 MDC 정보를 복사할 필요 없이 로그 추적을 쉽게 할 수 있습니다.