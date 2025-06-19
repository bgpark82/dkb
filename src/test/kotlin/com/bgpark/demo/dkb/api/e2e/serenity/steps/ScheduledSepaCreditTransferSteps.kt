package com.bgpark.demo.dkb.api.e2e.serenity.steps

import io.restassured.response.Response
import net.serenitybdd.annotations.Step

open class ScheduledSepaCreditTransferSteps {

    @Step("create a scheduled SEPA credit transfer for debtor IBAN {string} and creditor IBAN {string}")
    fun createScheduledSepaCreditTransfer(
        debtorIban: String,
        creditorIban: String,
        endToEndId: String,
    ) {
        val body = ScheduledSepaCreditTransferPostRequestBody().generateBody(
            debtorIban = debtorIban,
            creditorIban = creditorIban,
            endToEndId = endToEndId,
        )
        println(body)
    }

    class ScheduledSepaCreditTransferPostRequestBody {
        fun generateBody(
            debtorIban: String,
            creditorIban: String,
            endToEndId: String,
        ): String = """
            {
                "data": {
                    "type": "scheduledSepaCreditTransfer",
                    "attributes": {
                        "endToEndId": "$endToEndId",
                        "debtorIban": "$debtorIban",
                        "creditorIban": "$creditorIban",
                    }
                }
            }
        """.trimIndent()
    }
}