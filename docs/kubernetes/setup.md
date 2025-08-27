# Kubernetes로 Spring Boot 프로젝트 배포하기

이 가이드는 Spring Boot 애플리케이션과 데이터베이스를 Kubernetes에 배포하는 데 필요한 기본 파일 및 설정 방법을 설명합니다.

## 1. Dockerfile 생성

먼저, Spring Boot 애플리케이션을 컨테이너화하기 위한 `Dockerfile`을 생성해야 합니다. 프로젝트의 루트 디렉토리에 다음 내용으로 `Dockerfile`을 작성하세요.

```dockerfile
# 베이스 이미지로 OpenJDK 21 버전을 사용합니다.
FROM openjdk:21-jdk-slim

# 빌드된 JAR 파일의 경로를 변수로 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너의 app.jar로 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령을 지정합니다.
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 2. Docker 이미지 빌드 및 레지스트리 배포

Kubernetes에서 애플리케이션을 배포하려면, 먼저 애플리케이션을 Docker 이미지로 빌드하고 Docker Hub나 다른 컨테이너 레지스트리에 업로드해야 합니다.

### 2.1. 애플리케이션 빌드 (JAR 파일 생성)

**Gradle 사용 시:**
```bash
./gradlew build
```

**Maven 사용 시:**
```bash
./mvnw package
```

### 2.2. Docker 이미지 빌드

```bash
docker build -t <your-docker-hub-username>/<your-app-name>:<version> .
```

### 2.3. Docker 레지스트리 로그인

```bash
docker login docker.io
```

### 2.4. Docker 이미지 푸시

```bash
docker push <your-docker-hub-username>/<your-app-name>:<version>
```

## 3. Kubernetes에 데이터베이스 설정하기

`application.yml`에 정의된 PostgreSQL 데이터베이스를 Kubernetes 클러스터에 설정합니다.

### 3.1. 데이터베이스 암호화를 위한 Secret 생성

데이터베이스 사용자 이름과 비밀번호는 Secret 리소스를 사용하여 안전하게 저장합니다. 다음 내용으로 `deployment/db-secret.yaml` 파일을 작성하세요.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: postgres-secret
type: Opaque
stringData:
  POSTGRES_USER: myuser
  POSTGRES_PASSWORD: mypassword
```

### 3.2. 데이터베이스 설정을 위한 ConfigMap 생성

데이터베이스 이름과 같이 민감하지 않은 설정 정보는 ConfigMap을 사용하여 관리합니다. 다음 내용으로 `deployment/db-configmap.yaml` 파일을 작성하세요.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
data:
  POSTGRES_DB: mydatabase
```

### 3.3. 데이터 영속성을 위한 PersistentVolumeClaim 생성

데이터베이스의 데이터는 Pod이 재시작되어도 유지되어야 합니다. 이를 위해 PersistentVolumeClaim (PVC)을 사용하여 영구 저장 공간을 요청합니다. 다음 내용으로 `deployment/db-pvc.yaml` 파일을 작성하세요.

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: postgres-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

### 3.4. PostgreSQL Deployment 생성

PostgreSQL 데이터베이스를 실행할 Deployment를 생성합니다. 다음 내용으로 `deployment/db-deployment.yaml` 파일을 작성하세요.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
        - name: postgres
          image: postgres:14-alpine
          ports:
            - containerPort: 5432
          envFrom:
            - secretRef:
                name: postgres-secret
            - configMapRef:
                name: postgres-config
          volumeMounts:
            - name: postgres-storage
              mountPath: /var/lib/postgresql/data
      volumes:
        - name: postgres-storage
          persistentVolumeClaim:
            claimName: postgres-pvc
```

### 3.5. PostgreSQL Service 생성

Spring Boot 애플리케이션이 클러스터 내에서 데이터베이스에 접근할 수 있도록 Service를 생성합니다. 다음 내용으로 `deployment/db-service.yaml` 파일을 작성하세요.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
spec:
  selector:
    app: postgres
  ports:
    - protocol: TCP
      port: 5432
      targetPort: 5432
```

## 4. Kubernetes에 Spring Boot 애플리케이션 배포하기

### 4.1. Spring Boot Deployment 수정

Spring Boot 애플리케이션(`deployment/deployment.yaml`)이 새로 생성된 데이터베이스를 바라보도록 설정을 변경합니다. 데이터베이스 접속 정보를 환경 변수로 주입하여 `application.yml`의 설정을 덮어씁니다.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: spring-boot-app
  template:
    metadata:
      labels:
        app: spring-boot-app
    spec:
      containers:
      - name: spring-boot-app
        image: <your-docker-image> # Docker Hub 또는 다른 레지스트리의 이미지 주소로 변경하세요.
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres-service:5432/mydatabase
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: POSTGRES_USER
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: POSTGRES_PASSWORD
```

### 4.2. 애플리케이션 Service 생성

애플리케이션에 외부에서 접근할 수 있도록 Service를 생성합니다. 다음 내용으로 `deployment/service.yaml` 파일을 작성하세요.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-app-service
spec:
  selector:
    app: spring-boot-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```

### 4.3. Kubernetes에 배포

`deployment` 디렉토리의 모든 매니페스트 파일을 한 번에 적용하여 데이터베이스와 애플리케이션을 배포합니다.

```bash
kubectl apply -f deployment/
```

### 4.4. 배포 확인

```bash
# 모든 Deployment 상태 확인
kubectl get deployments

# 모든 Pod 상태 확인
kubectl get pods

# 모든 Service 상태 및 외부 IP 확인
kubectl get services
```
이제 `kubectl get services` 명령어로 확인된 `spring-boot-app-service`의 외부 IP 주소를 통해 Spring Boot 애플리케이션에 접근할 수 있습니다.

## 5. 트러블슈팅 (Troubleshooting)

### 5.1. `CreateContainerConfigError` 오류

애플리케이션 Pod에서 `CreateContainerConfigError` 또는 이와 유사한 오류가 발생하며 Pod이 정상적으로 시작되지 않는 경우가 있습니다.

```bash
# Pod 상태 확인 시
kubectl get pods

# NAME                               READY   STATUS                       RESTARTS   AGE
# spring-boot-app-5b7d7b6967-ndg84   0/1     CreateContainerConfigError   0          1m
```

**원인:**

이 오류는 Pod이 시작되는 데 필요한 `Secret`이나 `ConfigMap`을 찾지 못할 때 주로 발생합니다. Spring Boot 애플리케이션 Deployment는 `postgres-secret`이라는 `Secret`에 의존하고 있으므로, 이 `Secret`이 애플리케이션 Pod보다 먼저 생성되어야 합니다.

**해결 방법:**

1.  **Pod 상세 정보 확인:** 먼저, 다음 명령어로 Pod의 상세 이벤트(Events)를 확인하여 정확한 원인을 파악합니다.

    ```bash
    # `kubectl get pods` 명령어로 확인한 실제 Pod 이름으로 변경해야 합니다.
    kubectl describe pod <your-spring-boot-pod-name>
    ```

    명령어 결과의 Events 섹션에 `Secret "postgres-secret" not found` 와 같은 메시지가 있는지 확인합니다.

2.  **Secret 존재 여부 확인:**

    ```bash
    kubectl get secret postgres-secret
    ```
    만약 여기서 "not found" 결과가 나온다면 Secret이 생성되지 않은 것입니다.

3.  **올바른 배포 명령어 사용:**

    `deployment` 디렉토리의 모든 파일을 한 번에 적용하면 Kubernetes가 리소스 간의 의존성을 파악하여 올바른 순서로 생성합니다. 항상 아래 명령어를 사용하여 모든 설정을 한 번에 적용하는 것을 권장합니다.

    ```bash
    kubectl apply -f deployment/
    ```

    만약 이미 `apply`를 실행했다면, 다시 한번 위 명령어를 실행하여 누락된 리소스가 생성되도록 할 수 있습니다.
