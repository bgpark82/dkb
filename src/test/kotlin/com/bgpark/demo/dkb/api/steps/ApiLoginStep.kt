package com.bgpark.demo.dkb.api.steps

import com.bgpark.demo.dkb.api.ApiTestContext
import com.bgpark.demo.dkb.api.CucumberSpringConfiguration
import com.bgpark.demo.dkb.api.user.UserService
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.stereotype.Component
import org.springframework.test.context.ContextConfiguration

// Cucumber에서 Step은 @Component 필요없음. Cucumber가 Step을 자동으로 Bean으로 생성
//@Component
// @Autowired를 사용하기 위해 필요, 스프링 컨텍스트를 불러와야 하기 때문에
// 기본적으로 Cucumber는 Spring을 모름
@ContextConfiguration(classes = [CucumberSpringConfiguration::class])
class ApiLoginStep {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var apiTestContext: ApiTestContext

    @Autowired
    private lateinit var userService: UserService

    @Given("the application is running")
    fun theApplicationIsRunning() {
        // Set up RestAssured's base URI and port dynamically
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        // No specific action needed here, as Spring Boot Test handles application startup
        println("Application is running on port: $port")
    }

    @When("a POST request is sent to {string} with username {string} and password {string}")
    fun `a Post Request Is Sent To With Username And Password`(path: String, username: String, password: String) {
        val requestBody = mapOf(
            "username" to username,
            "password" to password
        )

        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .`when`()
            .post(path)

        response.body.prettyPrint()

        apiTestContext.lastResponse = response
    }

    @Then("the response status code should be {int}")
    fun theResponseStatusCodeShouldBe(expectedStatusCode: Int) {
        val actualStatusCode = apiTestContext.lastResponse?.statusCode
        assertEquals(expectedStatusCode, actualStatusCode, "Expected status code $expectedStatusCode but got $actualStatusCode")
    }

    @Then("the response body should contain {string}: {string}")
    fun theResponseBodyShouldContainString(jsonPath: String, expectedValue: String) {
        // Use RestAssured's JsonPath to extract the string value
        val actualValue = apiTestContext.lastResponse?.jsonPath()?.getString(jsonPath)
        assertEquals(expectedValue, actualValue, "Expected '$jsonPath' to be '$expectedValue' but got '$actualValue'")
    }
}