# Logback 설정을 통한 MDC 사용법

이 프로젝트에서 MDC(Mapped Diagnostic Context)를 사용하여 로그에 추가 정보를 남기려면, Spring Boot의 기본 로깅 설정을 오버라이드하는 `logback-spring.xml` 파일을 `src/main/resources` 디렉토리에 생성해야 합니다.

### `logback-spring.xml` 파일 생성

`src/main/resources/logback-spring.xml` 경로에 아래와 같은 내용으로 파일을 생성합니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <springProperty scope="context" name="LOG_LEVEL" source="logging.level.root" defaultValue="info"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [reqId:%X{requestId:-}] [actId:%X{activityId:-}] [payId:%X{paymentId:-}] [usrId:%X{userId:-}] %msg%n</pattern>
        </encoder>
    </appender>

    <root level="${LOG_LEVEL}">
        <appender-ref ref="STDOUT" />
    </root>

</configuration>
```

### 설정 설명

1.  **`<springProperty>`**: Spring Boot의 `application.yml` 또는 `application.properties` 파일에 정의된 속성을 가져와 Logback 설정에서 사용할 수 있게 합니다. 여기서는 `logging.level.root` 값을 `LOG_LEVEL` 변수로 가져옵니다.

2.  **`<appender>`**: 로그를 어디에 어떻게 출력할지 정의합니다.
    *   `STDOUT`: 콘솔(표준 출력)에 로그를 출력하는 appender입니다.
    *   `<encoder>`: 로그 메시지의 형식을 지정합니다.
    *   `<pattern>`: 로그 출력 패턴을 정의하는 핵심 부분입니다.
        *   `%d{...}`: 날짜 및 시간
        *   `[%thread]`: 현재 스레드 이름
        *   `%-5level`: 로그 레벨 (INFO, DEBUG, ERROR 등)
        *   `%logger{36}`: 로거 이름
        *   `[reqId:%X{requestId:-}] [actId:%X{activityId:-}] [payId:%X{paymentId:-}] [usrId:%X{userId:-}]`: **MDC를 사용하는 부분입니다.**
            *   `%X{key}` 형태로 MDC에 저장된 값을 가져옵니다. `key`는 애플리케이션 코드에서 `MDC.put("key", "value")`로 설정한 값입니다.
            *   `requestId`, `activityId`, `paymentId`, `userId`는 우리가 사용할 MDC 키의 예시입니다.
            *   `:-`는 해당 키에 대한 값이 MDC에 없을 경우 아무것도 출력하지 않도록 하는 기본값 설정입니다. 예를 들어, `requestId`가 MDC에 없다면 `[reqId:]` 부분은 로그에 나타나지 않습니다.

3.  **`<root>`**: 최상위 로거를 설정합니다.
    *   `level="${LOG_LEVEL}"`: `application.yml`에서 가져온 로그 레벨을 적용합니다.
    *   `<appender-ref ref="STDOUT" />`: 위에서 정의한 `STDOUT` appender를 사용하도록 설정합니다.

이제 애플리케이션 코드에서 `MDC.put("requestId", "...")`, `MDC.put("activityId", "...")` 등을 호출하면, 로그가 출력될 때마다 해당 값들이 로그 패턴에 맞게 포함되어 출력됩니다.