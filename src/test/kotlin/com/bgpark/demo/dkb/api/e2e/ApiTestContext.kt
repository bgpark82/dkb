package com.bgpark.demo.dkb.api.e2e

import io.restassured.response.Response
import org.springframework.stereotype.Component

/**
 * IMPORTANT: Serenity 테스트에도 설정이 필요하다
 * Share `response` object received from RestAssured between When and Then steps
 * ApiTestContext will store the response from RestAssured
 * Each step methods from Cucumber are executed separately
 * If you want to share the state between steps, you should use the same object by using dependency injection
 * But, ApiTestContext는 태스트 마다 새로운 컨텍스트를 만드는게 아님
 * - 테스트 간 격리를 보장하려면 @DirtyContext, @TestInstace를 사용, @ScenarioScope: 각 시나리오마다 새로 생성
 * - 같은 Step 클래스에 When, Then을 정의한다면 Context가 필요없음 -> 하지만 이렇게 사용하지 않기 때문에 shared state가 필요한 거겠지?\
 */
@Component
class ApiTestContext {

    var lastResponse: Response? = null
}