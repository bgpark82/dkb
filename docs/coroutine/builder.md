## `runBlocking`

runBlocking은 코루틴 빌더, launch와 async와 코루틴을 생성. 현재 스레드를 블록

`runBlocking`은 일반적인 `fun main()`과 같은 코루틴이 아닌 세계와 `runBlocking { ... }` 람다 내부의 코루틴 코드를 연결하는 코루틴 빌더입니다. 주로 `main` 함수와 테스트에서 사용하기 위한 것입니다.

`runBlocking`은 새 코루틴을 실행하고 완료될 때까지 현재 스레드를 *중단 가능하게(interruptibly)* **차단(block)**합니다. 이 함수는 코루틴 내에서 사용해서는 안 됩니다. 일반적인 블로킹 코드를 일시 중단 스타일로 작성된 라이브러리와 연결하고, `main` 함수 및 테스트에서 사용하도록 설계되었습니다.

### `runBlocking`의 주요 특징:

*   **스레드 차단**: 다른 코루틴 빌더(`launch`, `async`)와 달리 `runBlocking`은 내부의 코루틴이 완료될 때까지 호출된 스레드를 차단합니다.
*   **코루틴으로의 다리**: 코루틴 세계로 들어가는 다리 역할을 합니다. 그 안에서 일시 중단 함수를 호출할 수 있습니다.
*   **사용 사례**: 주요 사용 사례는 애플리케이션의 `main` 함수와 일시 중단 함수를 테스트하기 위한 단위 테스트입니다.

### 예제:

```kotlin
fun main() = runBlocking { // this: CoroutineScope
    launch { // 스코프 안에서 새로운 코루틴을 실행합니다.
        delay(1000L)
        println("World!")
    }
    println("Hello")
}
```

이 예제에서 `main` 스레드는 `runBlocking`에 의해 차단됩니다. "Hello"가 먼저 출력된 다음 1초 지연 후 "World!"가 출력됩니다. 그 후 애플리케이션이 종료됩니다. `runBlocking`이 없으면 `main` 함수는 "World!"가 출력되기 전에 종료될 것입니다.



제공된 코루틴 빌더의 종류에 대한 설명과 함께 각 빌더의 코드 예시를 보여드릴게요.

### `launch`

`launch`는 결과를 반환하지 않는 코루틴을 시작할 때 사용합니다. "실행하고 잊어버리는(fire-and-forget)" 유형의 작업에 적합합니다. 예를 들어, 사용자의 클릭에 대한 UI 업데이트나, 백그라운드 로그 전송처럼 결과를 기다릴 필요 없는 작업에 쓰입니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    println("메인 루틴 시작")

    // launch를 사용해 결과를 기다리지 않는 코루틴 시작
    launch {
        println("백그라운드에서 작업 시작")
        delay(1000) // 비동기 작업 흉내
        println("백그라운드 작업 완료")
    }
    
    // 메인 루틴은 백그라운드 작업과 상관없이 계속 실행됩니다.
    println("메인 루틴은 계속 진행됩니다")
}
```

-----

### `async`

`async`는 결과를 반환하는 코루틴을 시작할 때 사용합니다. `Deferred<T>`라는 객체를 반환하며, 이 객체에 `.await()`를 호출해서 최종 결과를 얻습니다. 여러 작업을 병렬로 실행하고 그 결과를 합쳐야 할 때 유용합니다.

```kotlin
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val time = measureTimeMillis {
        // async를 사용해 두 작업을 동시에 시작
        val result1 = async { performTask(1000) }
        val result2 = async { performTask(1500) }

        println("두 작업이 동시에 시작되었습니다.")
        
        // .await()를 사용해 두 작업의 결과를 기다립니다.
        val finalResult = result1.await() + result2.await()

        println("최종 결과: $finalResult")
    }
    println("총 실행 시간: $time ms")
}

// 딜레이 후 값을 반환하는 suspend 함수
suspend fun performTask(delayMillis: Long): String {
    delay(delayMillis)
    return "작업 완료($delayMillis)"
}
```

-----

### `runBlocking`

`runBlocking`은 일반 함수에서 코루틴을 실행해야 할 때 진입점 역할을 합니다. 이 빌더는 **현재 스레드를 블록**하기 때문에, 블록 내의 모든 코루틴 작업이 완료될 때까지 다음 코드가 실행되지 않고 대기합니다.

```kotlin
import kotlinx.coroutines.*

fun main() {
    println("main() 함수 시작")

    // runBlocking은 이 블록이 완료될 때까지 메인 스레드를 블록
    runBlocking {
        println("runBlocking 내부 시작")
        delay(2000) // 2초 대기
        println("runBlocking 내부 종료")
    }

    println("runBlocking 종료 후 main() 함수 계속 진행")
}
```

-----

### `withContext`

`withContext`는 코루틴의 컨텍스트를 전환하면서 작업을 수행하는 데 사용됩니다. 특히 I/O 작업이나 CPU 연산처럼 다른 스레드에서 실행되어야 하는 코드를 실행하고, 그 결과를 현재 코루틴으로 다시 가져올 때 유용합니다.

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    println("현재 스레드: ${Thread.currentThread().name}")

    // withContext를 사용해 I/O 스레드로 전환
    val result = withContext(Dispatchers.IO) {
        println("I/O 스레드에서 작업 시작: ${Thread.currentThread().name}")
        delay(1000) // 네트워크 호출 가정
        "네트워크 응답 데이터"
    }

    println("다시 메인 스레드로 돌아옴: ${Thread.currentThread().name}")
    println("결과: $result")
}
```

이 예시들은 각 빌더의 주요 용도와 동작 방식을 명확하게 보여줍니다. `launch`와 `async`는 **동시성 작업을 시작**하는 데 사용되고, `runBlocking`과 `withContext`는 **코루틴의 실행 컨텍스트와 흐름을 제어**하는 데 사용된다는 것을 기억하면 좋습니다.
