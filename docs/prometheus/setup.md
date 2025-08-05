# Prometheus & Grafana 연동 설정 가이드

이 문서는 Docker Compose를 사용하여 Prometheus와 Grafana를 설정하고, 현재 Spring Boot 애플리케이션과 연동하여 메트릭을 시각화하는 방법을 안내합니다.

## 1. Spring Boot 애플리케이션 설정

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
    - 기본 사용자 이름: `admin`
    - 기본 비밀번호: `admin`
    - 처음 로그인 시 비밀번호를 변경하라는 메시지가 나옵니다.

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
