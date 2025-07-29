package com.bgpark.demo.dkb.faud.payment.service.model

enum class SourceSystem(val scope: String, val messageValue: String) {
    NOVA("banking-app", "BAPP"),
    GAIA("internet-banking", "WB"),
    PORSCHE_APP("porsche-app", "POR"),
    PORSCHE_WEB("porsche-app-web", "POR"),
    MILES_AND_MORE_APP("miles-and-more-app", "MM"),
    MILES_AND_MORE_WEB("miles-and-more-web", "MM"),
    HILTON_APP("hilton-app", "HIL"),
    HILTON_WEB("hilton-app-web", "HIL");
}
