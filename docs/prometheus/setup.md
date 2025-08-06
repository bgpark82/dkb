# Prometheus & Grafana 연동 설정 가이드

이 문서는 Docker Compose를 사용하여 Prometheus와 Grafana를 설정하고, 현재 Spring Boot 애플리케이션과 연동하여 메트릭을 시각화하는 방법을 안내합니다.

## 1. Spring Boot 애플리케이션 설정

### Spring Boot와 Micrometer를 이용한 메트릭 전송

Spring Boot 애플리케이션의 모든 메트릭을 Prometheus로 전송하기 위해 **Micrometer**와 **Actuator**를 사용합니다. Micrometer는 다양한 모니터링 시스템(Prometheus, Datadog 등)에 대한 메트릭 전송을 추상화해주는 라이브러리이며, Spring Boot 2.0부터 기본적으로 내장되어 있습니다.

#### 1. 의존성 추가

`build.gradle.kts` (또는 `pom.xml`)에 다음 두 가지 의존성이 포함되어 있는지 확인합니다.

-   `spring-boot-starter-actuator`: 애플리케이션의 상태를 모니터링하고 관리할 수 있는 다양한 엔드포인트를 제공합니다.
-   `micrometer-registry-prometheus`: Micrometer가 수집한 메트릭을 Prometheus가 이해할 수 있는 포맷으로 변환하고, 이를 노출하는 엔드포인트(`/actuator/prometheus`)를 활성화합니다.

**build.gradle.kts 예시:**
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    // ... 기타 의존성
}
```

#### 2. `application.yml` 설정

`src/main/resources/application.yml` 파일에 다음 설정을 추가하여 Prometheus 엔드포인트를 활성화하고 웹에 노출시킵니다.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "prometheus,health" # prometheus와 health 엔드포인트를 웹에 노출
  metrics:
    tags: # 모든 메트릭에 공통 태그(레이블) 추가
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true # HTTP 요청에 대한 히스토그램 메트릭 활성화
    enable:
      all: true
spring:
  application:
    name: my-dkb-app # 애플리케이션 이름 설정
```

**설정 설명:**

-   `management.endpoints.web.exposure.include`: Actuator가 제공하는 여러 엔드포인트 중 어떤 것을 HTTP를 통해 외부로 노출할지 결정합니다. `prometheus`를 포함시켜야 Prometheus 서버가 메트릭을 수집해갈 수 있습니다.
-   `management.metrics.tags.application`: 여기에 설정된 값은 Prometheus의 **레이블(label)**로 추가됩니다. 여러 애플리케이션을 모니터링할 때 특정 애플리케이션의 메트릭만 필터링하는 데 매우 유용합니다.
-   `distribution.percentiles-histogram.http.server.requests`: `http_server_requests_seconds` 메트릭에 대한 히스토그램 데이터를 생성하도록 설정합니다. 이를 통해 Grafana에서 응답 시간의 백분위수(percentile)를 시각화하거나 Heatmap을 그릴 수 있습니다.

위 설정을 완료하고 애플리케이션을 실행하면, `http://localhost:8080/actuator/prometheus` 경로로 접속했을 때 Prometheus 포맷으로 변환된 모든 메트릭(JVM, CPU, 메모리, HTTP 요청 등)을 확인할 수 있습니다. Prometheus는 이 엔드포인트를 주기적으로 스크래핑(scraping)하여 데이터를 수집합니다.



(내용 동일)

## 2. Prometheus 설정

(내용 동일)

## 3. Grafana 설정

Grafana는 Prometheus가 수집한 메트릭을 시각적으로 표현하기 위한 대시보드 도구입니다.

### 3.1. Grafana용 Docker Compose 설정 추가

`docker-compose.yml` 파일에 Grafana 서비스를 추가합니다.

```yaml
services:
  # ... 기존 서비스들 (postgres, prometheus) ...

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    restart: always
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=changeme # 원하는 비밀번호로 변경하세요
    volumes:
      - grafana-storage:/var/lib/grafana
    depends_on:
      - prometheus

volumes:
  # ... 기존 볼륨 ...
  grafana-storage: {}
```

- `image`: 최신 Grafana 이미지를 사용합니다.
- `ports`: 호스트의 3000번 포트를 컨테이너의 3000번 포트와 연결하여 Grafana 웹 UI에 접근할 수 있도록 합니다.
- `environment`: Grafana의 관리자 계정 정보를 설정합니다.
  - `GF_SECURITY_ADMIN_USER`: 관리자 사용자 이름을 설정합니다.
  - `GF_SECURITY_ADMIN_PASSWORD`: 관리자 비밀번호를 설정합니다. **보안을 위해 `changeme`를 다른 값으로 변경하는 것을 강력히 권장합니다.**
- `volumes`: `grafana-storage`라는 볼륨을 생성하여 Grafana의 데이터(대시보드, 데이터 소스 설정 등)를 영속적으로 저장합니다.
- `depends_on`: Prometheus가 먼저 실행된 후에 Grafana가 실행되도록 의존성을 설정합니다.

## 4. Docker Compose 전체 설정 (`docker-compose.yml`)

Prometheus와 Grafana를 모두 포함한 `docker-compose.yml` 파일의 전체 내용은 다음과 같습니다.

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    container_name: my_postgres
    restart: always
    environment:
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
      POSTGRES_DB: mydatabase
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - monitoring

  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
    networks:
      - monitoring

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    restart: always
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=changeme
    volumes:
      - grafana-storage:/var/lib/grafana
    depends_on:
      - prometheus
    networks:
      - monitoring

volumes:
  postgres_data:
  grafana-storage: {}

networks:
  monitoring:
    driver: bridge
```

- **네트워크 설정**: `monitoring`이라는 브리지 네트워크를 생성하여 Prometheus와 Grafana가 서로 통신할 수 있도록 설정합니다. Prometheus 설정(`prometheus.yml`)에서 Grafana가 Prometheus에 접근할 때 `http://prometheus:9090`과 같이 서비스 이름을 사용할 수 있습니다.

## 5. 실행 및 연동 확인

### 5.1. 전체 서비스 실행

1.  Spring Boot 애플리케이션을 로컬에서 실행합니다.
2.  터미널에서 다음 명령어를 실행하여 모든 서비스를 시작합니다.

    ```bash
    docker-compose up -d
    ```

### 5.2. Grafana 설정 및 대시보드 추가

1.  **Grafana 접속**: 웹 브라우저에서 `http://localhost:3000`으로 접속합니다.
    - 사용자 이름: `docker-compose.yml`에 설정한 `GF_SECURITY_ADMIN_USER` 값 (예: `admin`)
    - 비밀번호: `docker-compose.yml`에 설정한 `GF_SECURITY_ADMIN_PASSWORD` 값 (예: `changeme`)

2.  **Prometheus 데이터 소스 추가**
    - 왼쪽 메뉴에서 톱니바퀴 아이콘(Configuration) > **Data Sources**를 클릭합니다.
    - **Add data source** 버튼을 클릭하고 **Prometheus**를 선택합니다.
    - **Settings** 탭에서 다음 정보를 입력합니다.
      - **Name**: `Prometheus` (또는 원하는 이름)
      - **URL**: `http://prometheus:9090` (Docker 네트워크 내에서 서비스 이름으로 접근)
    - **Save & test** 버튼을 클릭하여 연결이 성공적인지 확인합니다. "Data source is working" 메시지가 나타나면 성공입니다.

3.  **Spring Boot 대시보드 추가**
    - Grafana는 다양한 사전 제작 대시보드를 가져와 사용할 수 있습니다. Spring Boot용으로 널리 사용되는 대시보드를 추가해 보겠습니다.
    - 왼쪽 메뉴에서 `+` 아이콘(Create) > **Import**를 클릭합니다.
    - **Import via grafana.com** 필드에 `4701`을 입력하고 **Load** 버튼을 클릭합니다. (JVM (Micrometer) Dashboard)
    - **Options** 섹션 하단에서 방금 추가한 `Prometheus` 데이터 소스를 선택합니다.
    - **Import** 버튼을 클릭하여 대시보드를 추가합니다.

이제 생성된 대시보드를 통해 JVM, CPU, 메모리 등 애플리케이션의 다양한 메트릭을 실시간으로 확인할 수 있습니다.
