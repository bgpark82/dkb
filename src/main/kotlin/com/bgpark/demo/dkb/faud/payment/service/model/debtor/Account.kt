package com.bgpark.demo.dkb.faud.payment.service.model.debtor

import com.bgpark.demo.dkb.faud.payment.service.model.Iban
import java.math.BigDecimal
import java.util.UUID

data class Account(
    val accountId: UUID,
    val iban: Iban? = null,
    val expectedBalance: BigDecimal?,
    val availableBalance: BigDecimal?,
    val currencyCode: String,
    val nearTimeBalance: BigDecimal? = null,
    val accountBalance: BigDecimal? = null
)
