package com.bgpark.demo.dkb.api.e2e.cucumber

import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

/**
* Junit5의 TestSuite를 정의
* 여러 테스트를 묶는다는 뜻
* - 같은 종류의 테스트를 그룹으로 관리하고 싶을때
* 선택한 테스트 엔진 (Cucumber, Junit) 등과 선택한 리소스/클래스 기반으로 테스트 실행
*/
@Suite
/**
* 어떤 feature 파일을 사용할지 지정
* Suite로 묶을 때 classpath 기반으로 묶음
 */
@SelectClasspathResource("features/login.feature")
/**
* Junit이 어떤 테스트 엔진을 사용하는지 지정
* Junit5는 3개의 모듈로 구성됨
* - Junit platform: 테스트를 실행하는 런타임 플랫폼 (gradle, intellij)
* - test engine: 어떤 종류의 테스트를 실행할지 결정 (junit, cucumber, spock)
* - Junit Jupyter: Junit5의 실제 API를 포함  (junit-jupiter-engine)
 */
@IncludeEngines("cucumber")
/**
* GLUE_PROPERTY_NAME: "cucumber.glue
* Glue:
*  - .feature 파일의 시나리오를 실제 코드(Step Definition)와 연결해주는 역할
*  - Cucumber가 .feature을 읽어서 실행할 때 Step Definition이 어디 있는지 알려줌 (Given -> @Given)
* Cucumber가 StepDefinition, Hooks, Context 설정 클래스 찾을 때 패키지 경로 명시
 */
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.bgpark.demo.dkb.api.e2e.cucumber.steps, com.bgpark.demo.dkb.api.e2e")
/**
* CUCUMBER_PLUGIN: "cucumber.plugin"
* PLUGIN: 테스트 결과를 다양한 형식으로 출력하는 확장기능
* 실행 시 리포트를 어떻 포멧으로 남길지 설정
*/
@ConfigurationParameter( // Configure Cucumber reports
    key = PLUGIN_PROPERTY_NAME,
    value = "pretty, html:build/cucumber-reports/api-cucumber.html, json:build/cucumber-reports/api-cucumber.json"
)
class CucumberRunner {
}