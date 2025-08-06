## Grafana 시각화와 Prometheus 연동

https://www.youtube.com/watch?v=_WdIlz33FKE&t=2096s

### Grafana 시각화 (Visualization)

Grafana의 핵심 기능은 데이터를 의미 있는 정보로 바꾸는 **시각화**입니다. Grafana는 다양한 **패널(Panel)**을 제공하여 데이터를 여러 형태로 표현할 수 있습니다.

- **Graph**: 시간에 따른 데이터 변화를 보여주는 가장 일반적인 패널입니다.
- **Stat**: 단일 수치(현재 값, 평균, 최대값 등)를 강조하여 보여줍니다.
- **Gauge / Bar Gauge**: 특정 값의 범위를 시각적으로 표현하여 현재 상태를 쉽게 파악할 수 있게 합니다.
- **Table**: 데이터를 표 형태로 깔끔하게 정리하여 보여줍니다.
- **Heatmap**: 시간대별 데이터 분포를 색상으로 표현하여 패턴을 분석하는 데 유용합니다.

이러한 패널들을 조합하여 **대시보드(Dashboard)**를 구성하고, 시스템의 전반적인 상태를 한눈에 파악할 수 있습니다.

### Prometheus 연동 및 예시

Grafana는 Prometheus와 완벽하게 연동되어, Prometheus가 수집한 메트릭 데이터를 강력하게 시각화할 수 있습니다.

**연동 과정:**
1.  **데이터 소스 추가**: Grafana 설정에서 Prometheus를 데이터 소스(Data Source)로 추가하고, Prometheus 서버의 주소(예: `http://prometheus:9090`)를 입력합니다.
2.  **대시보드 및 패널 생성**: 새 대시보드에 패널을 추가하고, 데이터 소스로 위에서 설정한 Prometheus를 선택합니다.
3.  **쿼리 작성**: 패널의 쿼리 편집기에서 **PromQL(Prometheus Query Language)**을 사용하여 원하는 메트릭을 조회합니다.

**예시: HTTP 요청 수 시각화**

애플리케이션의 `http_requests_total`이라는 메트릭(레이블 `job="my-app"`)을 Prometheus가 수집하고 있다고 가정해 보겠습니다.

- **목표**: `my-app` 잡(job)의 분당 평균 HTTP 요청 수를 그래프로 시각화하고 싶습니다.
- **PromQL 쿼리**: 
  ```promql
  rate(http_requests_total{job="my-app"}[5m])
  ```
- **설명**:
  - `http_requests_total{job="my-app"}`: `job` 레이블이 `my-app`인 모든 HTTP 요청 메트릭을 선택합니다.
  - `[5m]`: 최근 5분의 데이터를 대상으로 합니다.
  - `rate(...)`: 해당 시간 동안의 초당 평균 증가율을 계산합니다.

이 쿼리를 Grafana의 **Graph 패널**에 입력하면, 시간에 따른 초당 평균 요청 수의 변화를 보여주는 그래프가 그려집니다. 이를 통해 사용자는 트래픽 변화를 직관적으로 모니터링할 수 있습니다.

### 패널별 Prometheus 쿼리 예시

#### 1. Graph 패널

- **사용 사례**: 시간에 따른 서버 CPU 사용률(%) 변화를 모니터링합니다.
- **PromQL 쿼리**:
  ```promql
  (1 - avg(rate(node_cpu_seconds_total{job="my-app", mode="idle"}[5m])) by (instance)) * 100
  ```
- **설명**: `idle` 상태가 아닌 CPU 시간의 비율을 계산하여 인스턴스별 평균 CPU 사용률을 구합니다.

#### 2. Stat 패널

- **사용 사례**: 현재 활성화된 사용자 수를 표시합니다.
- **PromQL 쿼리**:
  ```promql
  sum(active_sessions{job="my-app"})
  ```
- **설명**: `active_sessions` 메트릭의 총합을 구해 현재 시스템에 접속 중인 전체 사용자 수를 단일 값으로 보여줍니다.

#### 3. Gauge 패널

- **사용 사례**: 특정 파일 시스템의 디스크 사용량을 시각적으로 표현합니다.
- **PromQL 쿼리**:
  ```promql
  (node_filesystem_size_bytes{mountpoint="/", job="my-app"} - node_filesystem_free_bytes{mountpoint="/", job="my-app"}) / node_filesystem_size_bytes{mountpoint="/", job="my-app"} * 100
  ```
- **설명**: 전체 디스크 공간에서 사용 가능한 공간을 뺀 후, 백분율로 변환하여 게이지 차트에 표시합니다. 최솟값(0)과 최댓값(100)이 명확하여 상태를 직관적으로 파악하기 좋습니다.

#### 4. Table 패널

- **사용 사례**: HTTP 요청 상태 코드(2xx, 4xx, 5xx)별로 5분간의 평균 요청 수를 표로 정리합니다.
- **PromQL 쿼리**:
  ```promql
  sum(rate(http_requests_total{job="my-app"}[5m])) by (code)
  ```
- **설명**: `code`(상태 코드) 레이블을 기준으로 그룹화하여 각 상태 코드별 초당 평균 요청 수를 계산하고, 그 결과를 테이블 형태로 보여줍니다.

#### 5. Heatmap 패널

- **사용 사례**: API 요청의 응답 시간 분포를 시각화하여 어떤 응답 시간대에 요청이 집중되는지 분석합니다.
- **PromQL 쿼리**:
  ```promql
  sum(rate(http_request_duration_seconds_bucket{job="my-app"}[1m])) by (le)
  ```
- **설명**: `http_request_duration_seconds_bucket` (히스토그램 버킷) 메트릭을 사용하여 시간대별, 응답 시간(le, less than or equal)대별 요청 수를 색상으로 표현합니다. 이를 통해 특정 시간대에 응답 시간이 길어지는 패턴을 쉽게 발견할 수 있습니다.

