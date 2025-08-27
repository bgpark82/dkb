## CoroutineScope (코루틴 스코프)

`CoroutineScope`는 코루틴이 실행되는 컨텍스트와 생명주기를 정의합니다. `launch`나 `async`와 같은 빌더로 생성된 모든 코루틴은 특정 스코프 내에서 실행됩니다.

### CoroutineScope의 주요 역할

1.  **생명주기 제어 (Lifecycle Control)**: 스코프는 자신에게 속한 모든 코루틴을 책임집니다. 만약 스코프가 취소(cancel)되면, 그 스코프 내에서 실행 중인 모든 자식 코루틴도 함께 취소됩니다. 이는 메모리 누수를 방지하는 데 매우 중요합니다. 예를 들어, 스코프가 UI 컴포넌트의 생명주기에 연결되어 있다면, UI 컴포넌트가 파괴될 때 모든 코루틴이 자동으로 취소됩니다.

2.  **구조화된 동시성 (Structured Concurrency)**: 스코프는 부모-자식 관계를 강제합니다. 다른 코루틴의 스코프 내에서 실행된 코루틴은 해당 부모의 자식이 됩니다. 부모 코루틴은 모든 자식 코루틴이 완료될 때까지 기다립니다. 이러한 계층 구조는 코드를 더 예측 가능하고 관리하기 쉽게 만들어 줍니다.

3.  **컨텍스트(Context) 제공**: 스코프는 코루틴이 실행될 스레드를 지정하는 `Dispatcher`나 예외를 처리하는 `CoroutineExceptionHandler`와 같은 실행 컨텍스트 정보를 담고 있습니다.

### 주요 스코프 종류

*   **`GlobalScope`**: 애플리케이션의 생명주기와 함께 동작하는 전역 스코프입니다. `GlobalScope`에서 시작된 코루틴은 특정 컴포넌트의 생명주기에 묶여있지 않아 리소스 누수를 유발하기 쉽기 때문에, 특별한 이유가 없는 한 사용이 권장되지 않습니다.
*   **`viewModelScope` (Android)**: `ViewModel` 클래스의 확장 속성으로, `ViewModel`이 소멸될 때 자동으로 취소되는 스코프입니다. `ViewModel`에서 시작하는 대부분의 작업에 권장되는 스코프입니다.
*   **`lifecycleScope` (Android)**: `Activity`나 `Fragment`와 같은 `Lifecycle` 객체에 연결된 스코프로, 해당 생명주기가 파괴될 때 코루틴을 취소합니다.

코루틴 스코프는 **코루틴의 생명주기를 관리하고 구조적 동시성(Structured Concurrency)을 보장**하는 역할을 합니다. 즉, 코루틴이 언제 시작되고, 언제 자동으로 취소되어야 하는지를 정의하는 '범위'라고 생각하면 됩니다.

코루틴 스코프의 주요 종류와 예시는 다음과 같습니다.

### 1\. `CoroutineScope`

가장 기본적인 코루틴 스코프입니다. 직접 생성하여 코루틴의 생명주기를 수동으로 제어할 수 있습니다. `CoroutineScope` 내에서 시작된 코루틴은 해당 스코프가 취소될 때 함께 취소됩니다.

**예시**:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // 새로운 CoroutineScope 생성
    val myScope = CoroutineScope(Dispatchers.Default)

    // myScope에서 10개의 코루틴 시작
    val jobs = List(10) { i ->
        myScope.launch {
            delay(100) // 짧은 지연
            println("작업 $i 완료")
        }
    }

    delay(200) // 200ms 후
    // 모든 작업을 취소합니다.
    myScope.coroutineContext.cancelChildren() 
    println("모든 작업이 취소되었습니다.")
}
```

이 예시에서는 `myScope` 내의 모든 `launch` 작업이 `cancelChildren()` 호출로 인해 일괄적으로 취소됩니다.

\<hr\>

### 2\. `GlobalScope`

애플리케이션 전체의 생명주기와 함께하는 전역 스코프입니다. `GlobalScope`에서 시작된 코루틴은 명시적으로 취소하지 않으면 애플리케이션이 종료될 때까지 계속 실행됩니다. 이는 예상치 못한 동작을 유발하거나 메모리 누수를 발생시킬 수 있어 **사용을 권장하지 않습니다.**

**예시**:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    println("GlobalScope 작업 시작")

    // GlobalScope에서 코루틴 시작
    GlobalScope.launch {
        delay(500)
        println("GlobalScope에서 실행된 작업 (앱 종료 전까지 계속)")
    }

    delay(100) // 메인 코루틴은 0.1초 후에 종료
    println("메인 루틴 종료")
}
```

위 코드를 실행하면 메인 루틴이 먼저 종료되지만, `GlobalScope`의 코루틴은 백그라운드에서 계속 실행되다가 지연 시간(500ms)이 끝난 후 메시지를 출력합니다.

\<hr\>

### 3\. `coroutineScope`

**구조적 동시성**을 보장하는 스코프입니다. `coroutineScope`는 내부의 모든 자식 코루틴이 완료될 때까지 **현재 코루틴을 일시 정지(suspend)시킵니다.** 자식 코루틴 중 하나라도 실패하면 다른 모든 자식 코루틴과 함께 `coroutineScope` 자체가 취소됩니다.

**예시**:

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun fetchTwoThings() = coroutineScope {
    val time = measureTimeMillis {
        val thing1 = async {
            delay(1500)
            println("첫 번째 작업 완료")
            "데이터1"
        }
        val thing2 = async {
            delay(1000)
            println("두 번째 작업 완료")
            "데이터2"
        }
        
        // 두 async 작업이 모두 완료될 때까지 coroutineScope가 일시 정지
        println("결과: ${thing1.await()}, ${thing2.await()}")
    }
    println("총 소요 시간: $time ms")
}

fun main() = runBlocking {
    fetchTwoThings()
}
```

`coroutineScope` 내의 `async` 작업들이 병렬로 실행되고, `coroutineScope`는 두 작업이 모두 완료될 때까지 `suspend` 상태로 기다립니다. `main` 함수의 `fetchTwoThings()`가 동기적으로 보이는 이유가 바로 이 때문입니다.

\<hr\>

### 4\. `supervisorScope`

`coroutineScope`와 비슷하지만, **자식 코루틴의 실패가 다른 자식 코루틴에 영향을 미치지 않도록** 합니다. 하나의 자식 코루틴이 실패하더라도 다른 자식 코루틴들은 계속 실행됩니다.

**예시**:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    supervisorScope {
        val job1 = launch {
            try {
                println("첫 번째 작업 시작")
                delay(500)
                throw Exception("고의적인 실패!")
            } finally {
                println("첫 번째 작업의 finally 블록 실행")
            }
        }

        val job2 = launch {
            try {
                delay(1000)
                println("두 번째 작업 완료")
            } finally {
                println("두 번째 작업의 finally 블록 실행")
            }
        }
    }
    println("supervisorScope가 종료되었습니다.")
}
```

`job1`에서 예외가 발생하더라도 `supervisorScope`는 이를 무시하고 `job2`가 계속 실행될 수 있도록 합니다. 이 특성은 여러 독립적인 작업을 처리할 때 유용합니다.
