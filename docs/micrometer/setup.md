# Micrometer 설정 및 메트릭 노출 방법

Spring Boot 애플리케이션에서 Micrometer를 설정하고, 수집된 메트릭을 특정 URL 엔드포인트를 통해 노출하는 방법은 매우 간단합니다. 여기서는 가장 널리 사용되는 모니터링 시스템 중 하나인 **Prometheus**와 연동하는 과정을 예시로 설명합니다.

### 1단계: 의존성 추가

먼저, 필요한 라이브러리를 프로젝트의 `build.gradle.kts` 파일에 추가해야 합니다.

1.  **`spring-boot-starter-actuator`**: Spring Boot 애플리케이션의 상태를 모니터링하고 관리하는 다양한 기능을 제공합니다. 메트릭, 상태(health), 정보(info) 등의 엔드포인트를 포함합니다.
2.  **`micrometer-registry-prometheus`**: Micrometer가 수집한 메트릭을 Prometheus가 이해할 수 있는 텍스트 형식으로 변환하고, 이를 노출하는 엔드포인트를 활성화하는 역할을 합니다.

```kotlin
// build.gradle.kts

dependencies {
    // ... 다른 의존성들 ...

    // 1. Spring Boot Actuator 추가
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 2. Micrometer의 Prometheus Registry 추가
    implementation("io.micrometer:micrometer-registry-prometheus")
}
```

### 2단계: `application.yml` 설정

의존성을 추가한 후, `src/main/resources/application.yml` 파일을 수정하여 Actuator 엔드포인트를 활성화하고 외부에 노출해야 합니다.

```yaml
# application.yml

management:
  endpoints:
    web:
      exposure:
        # 외부에 노출할 엔드포인트를 지정합니다.
        # "prometheus"를 포함시켜야 메트릭 엔드포인트가 활성화됩니다.
        # 쉼표로 구분하여 여러 엔드포인트를 노출할 수 있습니다. (예: "health,info,prometheus")
        include: prometheus, health

  endpoint:
    health:
      # /actuator/health 엔드포인트에 상세 정보를 표시할지 여부
      show-details: always

  metrics:
    tags:
      # 모든 메트릭에 공통적으로 적용될 태그(Tag)를 설정합니다.
      # 애플리케이션을 식별하는 데 매우 유용합니다.
      application: ${spring.application.name:my-app} # spring.application.name이 없으면 my-app 사용
```

**주요 설정 설명:**

-   `management.endpoints.web.exposure.include`: Actuator가 제공하는 여러 엔드포인트 중 HTTP를 통해 외부에 노출할 엔드포인트의 ID 목록을 지정합니다. `prometheus`를 추가하면 `/actuator/prometheus` 경로가 활성화됩니다.
-   `management.metrics.tags.application`: 여기에 설정된 태그는 Micrometer가 수집하는 모든 메트릭에 자동으로 추가됩니다. 여러 애플리케이션의 메트릭을 하나의 대시보드에서 볼 때, 특정 애플리케이션의 메트릭만 필터링하는 데 필수적인 설정입니다.

### 3단계: 애플리케이션 실행 및 확인

설정을 마친 후 애플리케이션을 실행합니다.

그리고 웹 브라우저나 `curl` 같은 도구를 사용하여 아래 URL에 접속합니다.

```bash
# curl을 사용한 확인
curl http://localhost:8080/actuator/prometheus
```

접속에 성공하면, 다음과 같이 Prometheus가 수집할 수 있는 텍스트 형식의 메트릭들이 출력되는 것을 볼 수 있습니다.

```text
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Survivor Space",} 2.4159232E7
jvm_memory_used_bytes{area="heap",id="G1 Old Gen",} 5.8720256E7
# ... (수많은 다른 메트릭들)

# HELP http_server_requests_seconds  
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 1.0
http_server_requests_seconds_sum{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/actuator/prometheus",} 0.0428134

# ... etc
```

이제 Prometheus 서버가 이 `/actuator/prometheus` 엔드포인트를 주기적으로 수집(scrape)하도록 설정하면, 애플리케이션의 메트릭을 시각화하고 모니터링할 수 있습니다.
