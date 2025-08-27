# 베이스 이미지로 OpenJDK 21 버전을 사용합니다.
FROM openjdk:21-jdk-slim

# 빌드된 JAR 파일의 경로를 변수로 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너의 app.jar로 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 명령을 지정합니다.
ENTRYPOINT ["java","-jar","/app.jar"]
