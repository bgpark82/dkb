package com.bgpark.demo.dkb.api.e2e.serenity.steps

import io.cucumber.java.en.When
import net.serenitybdd.annotations.Steps

open class ScheduledSepaCreditTransferStepDef {

    @Steps
    lateinit var scheduledSepaCreditTransferSteps: ScheduledSepaCreditTransferSteps

    @When("create a scheduled SEPA credit transfer with")
    fun `create a scheduled sepa credit transfer with`(parameterMap: Map<String, String>) {
        scheduledSepaCreditTransferSteps.createScheduledSepaCreditTransfer(
            parameterMap.getOrDefault("debtorIban", "debtorIban"),
            parameterMap.getOrDefault("creditorIban", "creditorIban"),
            parameterMap.getOrDefault("endToEndId", "endToEndId"),
        )
    }
}