package com.bgpark.demo.dkb.api.e2e.serenity

import io.cucumber.junit.CucumberOptions
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
//@SelectClasspathResource("/features")
@SelectClasspathResource("features/ScheduleSepaCreditTransferTest.feature")
@CucumberOptions(
    features = ["src/test/resources/features"],
    glue = ["steps"]
)
class SerenityCucumberRunnerV2 {
}