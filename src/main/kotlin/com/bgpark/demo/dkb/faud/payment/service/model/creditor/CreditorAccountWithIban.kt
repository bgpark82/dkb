package com.bgpark.demo.dkb.faud.payment.service.model.creditor

import com.bgpark.demo.dkb.faud.payment.service.model.Iban

data class CreditorAccountWithIban(
    val iban: Iban,
    val bic: String? = null,
) : CreditorAccount