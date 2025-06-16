# Makefile for Spring Boot (Kotlin/Gradle) and Docker Build

# 변수 정의
# Docker 이미지 이름. 원하는 이름으로 변경하세요.
IMAGE_NAME := bgpark82/sepa-credit-transfer-api
# Docker 이미지 태그. 버전 관리에 유용합니다.
IMAGE_TAG := 1.0

.PHONY: all build push clean

# 'all' 타겟: 기본적으로 실행되며, 빌드와 푸시를 순차적으로 수행합니다.
all: build push

# 'build' 타겟: Spring Boot 애플리케이션을 빌드하고 Docker 이미지를 생성합니다.
build:
	@echo "--- Building Spring Boot JAR ---"
	./gradlew bootJar -x test
	@echo "--- Building Docker Image: $(IMAGE_NAME):$(IMAGE_TAG) ---"
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

# 'push' 타겟: 생성된 Docker 이미지를 Docker Hub에 푸시합니다.
push:
	@echo "--- Pushing Docker Image: $(IMAGE_NAME):$(IMAGE_TAG) to Docker Hub ---"
	docker push $(IMAGE_NAME):$(IMAGE_TAG)

# 'clean' 타겟: Gradle 빌드 디렉토리를 정리합니다.
clean:
	@echo "--- Cleaning Gradle build artifacts ---"
	./gradlew clean

minikube-start:
	minikube start

tunnel:
	minikube tunnel

kubectl-start:
	kubectl apply -f sepa-credit-transfer-api-deployment.yml

helm-creat:
	helm create sepa-credit-transfer-api

helm-lint:
	cd sepa-credit-transfer-api && \
	helm lint .

helm-install:
	cd sepa-credit-transfer-api && \
    helm install my-sepa-app .
