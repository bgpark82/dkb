# MDC (Mapped Diagnostic Context)

MDC는 "매핑된 진단 컨텍스트(Mapped Diagnostic Context)"의 약자입니다. SLF4J 로깅 라이브러리에서 제공하는 기능으로, 로그 메시지에 추가적인 컨텍스트 정보를 담아 디버깅 및 트러블슈팅을 용이하게 만들어 줍니다. 특히 여러 클라이언트의 요청을 동시에 처리하는 멀티스레드 환경에서 매우 유용합니다.

## 주요 목적

MDC의 핵심 목적은 서로 다른 출처(예: 다른 사용자 요청)에서 뒤섞여 출력되는 로그를 명확하게 구분하는 것입니다. 각 요청에 고유한 "스탬프"를 찍는 것과 같습니다.

주요 사용 사례는 다음과 같습니다.

- **요청 추적 (Request Tracing):** 고유한 요청 ID(Request ID)를 로그에 추가하여, 분산 시스템을 포함한 애플리케이션의 여러 부분에 걸친 요청의 전체 생명주기를 추적합니다.
- **사용자 컨텍스트 (User Context):** 사용자 ID 또는 세션 ID를 포함시켜 특정 사용자의 활동과 관련된 로그만 필터링할 수 있습니다.
- **스레드 디버깅 (Debugging Threads):** 여러 스레드에서 생성된 로그 항목을 구분합니다.

## 작동 원리

MDC는 스레드 로컬(thread-local) 맵을 사용하여 키-값 쌍을 저장합니다. 즉, 각 스레드는 자신만의 독립적인 MDC 맵을 가지므로 스레드 간섭 없이 안전하게 사용할 수 있습니다. 스레드의 MDC에 키-값 쌍을 추가하면, 해당 스레드에서 이후에 생성되는 모든 로그 문은 그 값에 접근할 수 있습니다.

`org.slf4j.MDC` 클래스는 컨텍스트 관리를 위한 정적(static) 메서드를 제공합니다.

- `MDC.put(String key, String val)`: 현재 스레드의 컨텍스트 맵에 값을 추가합니다.
- `MDC.get(String key)`: 컨텍스트에서 값을 조회합니다.
- `MDC.remove(String key)`: 컨텍스트에서 값을 제거합니다.
- `MDC.clear()`: 스레드의 컨텍스트 맵에 있는 모든 항목을 지웁니다.

## 사용 예시

웹 애플리케이션에서 MDC를 사용하는 일반적인 흐름입니다.

1.  **요청 시작 시점:** 서블릿 필터(Filter)나 인터셉터(Interceptor) 등에서 요청이 들어올 때 MDC에 컨텍스트 정보를 추가합니다.

    ```java
    import org.slf4j.MDC;
    import java.util.UUID;

    // 요청 처리 시작 부분
    MDC.put("requestId", UUID.randomUUID().toString());
    MDC.put("userIp", request.getRemoteAddr());
    ```

2.  **로깅 설정:** 로깅 프레임워크(예: `logback.xml`)의 패턴에 MDC 값을 포함시킵니다. 보통 `%X{key}` 구문을 사용합니다.

    ```xml
    <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - [%X{requestId}] %msg%n</pattern>
    ```

3.  **요청 종료 시점:** 요청 처리가 완료되면, 반드시 `MDC.clear()`를 호출하여 컨텍스트를 정리해야 합니다. 정리하지 않으면 스레드 풀(Thread Pool)에 의해 재사용되는 스레드가 이전 요청의 MDC 값을 가지게 되어 로그가 오염될 수 있습니다.

    ```java
    // 요청 처리 종료 부분
    MDC.clear();
    ```
