# JRE를 포함하는 경량화된 OpenJDK 21 이미지 사용 (Java 21)
FROM openjdk:21-jdk-slim

# 애플리케이션 JAR 파일명을 정의합니다.
# Gradle의 bootJar 태스크는 일반적으로 'build/libs' 디렉토리에 JAR 파일을 생성합니다.
ARG JAR_FILE=build/libs/*.jar

# 빌드된 JAR 파일을 컨테이너에 복사합니다.
COPY ${JAR_FILE} app.jar

# 컨테이너가 시작될 때 실행될 명령어입니다.
ENTRYPOINT ["java","-jar","/app.jar"]

# Spring Boot 애플리케이션이 사용할 포트를 노출합니다.
EXPOSE 8080