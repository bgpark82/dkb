package com.bgpark.demo.dkb.api.cucumber

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest

// Cucumber가 Spring Test Context를 인식하여 빈을 사용 가능
// Cucumber의 StepDefinition 클래스에 @Autowired, @Value 등 스프링 빈 주입을 사용할 떄
@CucumberContextConfiguration
// SpringBootTest: load entire context
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// ContextConfiguration: Load certain context (bean)
//@ContextConfiguration(classes = [DkbApplication::class, UserService::class, UserController::class])
class CucumberSpringConfiguration {

}