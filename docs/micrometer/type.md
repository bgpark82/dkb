# Micrometer 메트릭 타입 상세 설명

Micrometer는 다양한 유형의 메트릭을 수집하기 위해 여러 `Meter` 타입을 제공합니다. 각 타입은 특정 시나리오에 맞춰 설계되었으며, 애플리케이션의 상태를 효과적으로 관찰하기 위해서는 올바른 타입을 선택하는 것이 중요합니다.

### 1. Counter

`Counter`는 **단조롭게 증가(monotonically increasing)하는 누적 값**을 기록하기 위한 메트릭입니다. 즉, 값은 오직 증가하거나 0으로 초기화될 수만 있으며, 감소하지 않습니다. "지금까지 발생한 총횟수"를 측정하는 데 사용됩니다.

- **주요 용도:**
    - 총 HTTP 요청 수
    - 처리된 메시지 총량
    - 특정 예외가 발생한 총 횟수
    - 캐시 hit/miss 횟수
- **수집 데이터:** 단일 `double` 값 (총 카운트)
- **특징:** 모니터링 시스템은 수집된 `Counter` 값의 시간당 변화율(rate)을 계산하여 "초당 요청 수(RPS)"와 같은 데이터를 보여주는 경우가 많습니다.

**Kotlin 예제:**
```kotlin
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry

fun processOrder(registry: MeterRegistry) {
    // "orders.processed" 라는 이름의 Counter를 가져오거나 생성하고, "status" 태그를 추가합니다.
    val processedCounter: Counter = registry.counter("orders.processed", "status", "success")

    // Counter 값을 1 증가시킵니다.
    processedCounter.increment()
    
    // 특정 값만큼 증가시킬 수도 있습니다.
    // processedCounter.increment(10.0)
}
```

### 2. Gauge

`Gauge`는 **특정 시점의 값(current value)**을 측정하기 위한 메트릭입니다. 값은 오르내릴 수 있으며, 현재 상태를 그대로 보여주는 계기판과 같습니다.

- **주요 용도:**
    - 현재 활성 사용자 수
    - 큐(Queue)에 쌓여있는 메시지 개수
    - 캐시의 현재 크기
    - CPU 온도 또는 메모리 사용량
- **수집 데이터:** 단일 `double` 값 (현재 값)
- **특징:** `Gauge`는 값을 직접 저장하지 않고, 값을 반환하는 함수나 객체를 등록하여 주기적으로 해당 값을 샘플링(sampling)해 갑니다. 따라서 `Gauge`에 등록된 객체는 가비지 컬렉션(GC)의 대상이 되지 않도록 강한 참조(strong reference)로 유지되어야 합니다.

**Kotlin 예제:**
```kotlin
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import java.util.concurrent.ConcurrentLinkedQueue

// Gauge로 측정할 대상 (예: 메시지 큐)
val messageQueue = ConcurrentLinkedQueue<String>()

fun registerQueueSizeGauge(registry: MeterRegistry) {
    // messageQueue::size 함수를 등록하여 큐의 현재 크기를 Gauge로 측정합니다.
    Gauge.builder("queue.size", messageQueue::size)
        .description("현재 큐에 쌓인 메시지 수")
        .tag("name", "orderQueue")
        .register(registry)
}
```

### 3. Timer

`Timer`는 **짧은 시간 동안 발생하는 이벤트의 횟수와 총 소요 시간**을 함께 측정합니다. 주로 마이크로초(microseconds)나 밀리초(milliseconds) 단위의 지연 시간(latency)을 측정하는 데 사용됩니다.

- **주요 용도:**
    - HTTP API 요청의 응답 시간
    - 데이터베이스 쿼리 실행 시간
    - 메소드 실행 시간
- **수집 데이터:**
    - 이벤트 발생 횟수 (`count`)
    - 모든 이벤트의 총 소요 시간 (`totalTime`)
    - 관측된 최대 소요 시간 (`max`)
    - 백분위수(Percentiles), 히스토그램(Histogram) 등 분포 통계 (설정에 따라)
- **특징:** 성능 분석에 매우 유용한 풍부한 통계 정보를 제공합니다.

**Kotlin 예제:**
```kotlin
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer

fun handleHttpRequest(registry: MeterRegistry) {
    val timer: Timer = registry.timer("http.requests", "uri", "/api/users")

    // timer.record()에 람다를 전달하면, 해당 람다의 실행 시간을 자동으로 측정합니다.
    timer.record {
        // 여기에 실제 비즈니스 로직을 넣습니다.
        Thread.sleep(150)
    }
}
```

### 4. LongTaskTimer

`LongTaskTimer`는 `Timer`와 유사하지만, **아직 완료되지 않은 장기 실행 작업(long-running task)의 수와 총 진행 시간**을 측정하는 데 특화되어 있습니다. `Timer`가 완료된 이벤트를 기록하는 반면, `LongTaskTimer`는 현재 진행 중인 이벤트를 추적합니다.

- **주요 용도:**
    - ETL(Extract, Transform, Load) 작업 모니터링
    - 대용량 파일 처리나 배치(batch) 작업 모니터링
- **수집 데이터:**
    - 현재 활성(진행 중인) 작업의 수 (`activeTasks`)
    - 활성 작업들의 총 진행 시간 (`duration`)
- **특징:** "현재 얼마나 많은 작업이 얼마나 오래 실행되고 있는가?"에 대한 답을 줍니다.

**Kotlin 예제:**
```kotlin
import io.micrometer.core.instrument.LongTaskTimer
import io.micrometer.core.instrument.MeterRegistry

fun runBatchJob(registry: MeterRegistry) {
    val longTaskTimer: LongTaskTimer = registry.newLongTaskTimer("batch.job", "type", "dataProcessing")

    // 작업을 시작할 때 샘플을 시작합니다.
    val sample: LongTaskTimer.Sample = longTaskTimer.start()

    try {
        // 오래 걸리는 작업 수행
        Thread.sleep(10000)
    } finally {
        // 작업이 끝나면 샘플을 중지합니다.
        sample.stop()
    }
}
```

### 5. DistributionSummary

`DistributionSummary`는 **시간과 관련 없는 값의 분포**를 추적하기 위해 사용됩니다. `Timer`가 시간 값의 분포를 측정하는 데 특화되어 있다면, `DistributionSummary`는 일반적인 숫자 값의 분포를 측정합니다.

- **주요 용도:**
    - HTTP 요청의 페이로드(payload) 크기 분포
    - 큐에 추가된 메시지의 크기 분포
- **수집 데이터:** `Timer`와 유사하게 `count`, `totalAmount`, `max` 및 분포 통계를 제공합니다.
- **특징:** `Timer`는 시간을 다루는 `DistributionSummary`의 특수화된 버전이라고 볼 수 있습니다.

**Kotlin 예제:**
```kotlin
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry

fun receiveMessage(registry: MeterRegistry, messagePayload: ByteArray) {
    val summary: DistributionSummary = registry.summary("message.size", "direction", "inbound")

    // 메시지 페이로드의 크기를 기록합니다.
    summary.record(messagePayload.size.toDouble())
}
```
