package com.bgpark.demo.dkb.api.e2e.serenity.steps

import io.cucumber.java.en.Given

//@Component
// @Autowired를 사용하기 위해 필요, 스프링 컨텍스트를 불러와야 하기 때문에
// 기본적으로 Cucumber는 Spring을 모름
//@ContextConfiguration(classes = [CucumberSpringConfiguration::class])
open class GeneralStepDef {

    @Given("an access user token")
    fun getValidAccessToken() {
        println("get valid access token")
    }
}