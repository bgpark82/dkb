package com.bgpark.demo.dkb.api.e2e.serenity.steps

import com.bgpark.demo.dkb.api.e2e.ApiTestContext
import com.bgpark.demo.dkb.api.e2e.CucumberSpringConfiguration
import io.restassured.RestAssured
import io.restassured.http.ContentType
import net.serenitybdd.annotations.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration

/**
 * Action
 * - RestAssured 캡슐화
 * - @Step 어노테이션으로 보고서에 상세 단계를 기록
 */
// Glue 클래스는 Cucumber가 직접 생성하고 로드한다
// GLue 클래스 : 시나리오 파일==feature 파일을 실행할 때 실제 동작을 정의한 클래스
// @Component가 붙어 있으면 Spring에서도 빈을 스킨하려고해서 충돌발생, Serenity가 생성하도록 놔둬야 함
// SpringContext안에서 로드 안된다
@ContextConfiguration(classes = [CucumberSpringConfiguration::class])
class LoginApiActions {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var apiTestContext: ApiTestContext

    @Step("Sends a POST request to {0} with username {1} and password {2}")
    fun sendLoginRequest(path: String, username: String, password: String) {
        // RestAssured의 기본 URI와 포트를 동적으로 설정합니다.
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port

        val requestBody = mapOf(
            "username" to username,
            "password" to password
        )

        val response = RestAssured.given()
            .contentType(ContentType.JSON) // 요청 본문 타입 지정
            .body(requestBody)             // 요청 본문 설정
            .`when`()                      // RestAssured 구문
            .post(path)                    // POST 요청 수행

        apiTestContext.lastResponse = response // 응답을 공유 컨텍스트에 저장
    }
}