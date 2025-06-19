package com.bgpark.demo.dkb.api.e2e.serenity.steps

import com.bgpark.demo.dkb.api.e2e.ApiTestContext
import com.bgpark.demo.dkb.api.e2e.CucumberSpringConfiguration
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import net.serenitybdd.annotations.Steps
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration

//@Component
// @Autowired를 사용하기 위해 필요, 스프링 컨텍스트를 불러와야 하기 때문에
// 기본적으로 Cucumber는 Spring을 모름
@ContextConfiguration(classes = [CucumberSpringConfiguration::class])
class ApiLoginSteps {

    // Serenity가 인스턴스를 주입하고 관리
    @Steps
    private lateinit var loginApiActions: LoginApiActions

    @Autowired
    private lateinit var apiTestContext: ApiTestContext // 공유 컨텍스트 주입

    @Given("the application is running")
    fun theApplicationIsRunning() {
        // Spring Boot Test가 애플리케이션을 자동으로 시작하므로 명시적인 액션은 필요 없습니다.
    }

    @When("a POST request is sent to {string} with username {string} and password {string}")
    fun aPostRequestIsSentToWithUsernameAndPassword(path: String, username: String, password: String) {
        loginApiActions.sendLoginRequest(path, username, password)
    }

    @Then("the response status code should be {int}")
    fun theResponseStatusCodeShouldBe(expectedStatusCode: Int) {
        val actualStatusCode = apiTestContext.lastResponse?.statusCode
        assertEquals(expectedStatusCode, actualStatusCode, "Expected status code $expectedStatusCode but got $actualStatusCode")
    }

//    @Then("the response body should contain {string}: {boolean}")
//    fun theResponseBodyShouldContainBoolean(jsonPath: String, expectedValue: Boolean) {
//        val actualValue = apiTestContext.lastResponse?.jsonPath()?.getBoolean(jsonPath)
//        assertEquals(expectedValue, actualValue, "Expected '$jsonPath' to be $expectedValue but got $actualValue")
//    }

    @Then("the response body should contain {string}: {string}")
    fun theResponseBodyShouldContainString(jsonPath: String, expectedValue: String) {
        val actualValue = apiTestContext.lastResponse?.jsonPath()?.getString(jsonPath)
        assertEquals(expectedValue, actualValue, "Expected '$jsonPath' to be '$expectedValue' but got '$actualValue'")
    }
}