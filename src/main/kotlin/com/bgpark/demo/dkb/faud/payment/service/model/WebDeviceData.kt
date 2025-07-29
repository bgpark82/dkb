package com.bgpark.demo.dkb.faud.payment.service.model

import java.time.ZonedDateTime
import java.util.UUID

data class WebDeviceData(
    var id: UUID,
    val mfaId: UUID,
    val createdAt: ZonedDateTime,
//    val deviceInfo: WebDeviceInfo,
//    val ipInfo: WebIpInfo,
//    val browserInfo: WebBrowserInfo
)