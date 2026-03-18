package com.proscan.generator_domain.model

sealed class QrGenerateRequest {
    data class TextRequest(val text: String) : QrGenerateRequest()
    data class UrlRequest(val url: String) : QrGenerateRequest()
    data class PhoneRequest(val phone: String) : QrGenerateRequest()
    data class EmailRequest(val to: String, val subject: String = "", val body: String = "") : QrGenerateRequest()
    data class SmsRequest(val phone: String, val message: String = "") : QrGenerateRequest()
    data class ContactRequest(
        val name: String,
        val phone: String = "",
        val email: String = "",
        val org: String = "",
        val address: String = ""
    ) : QrGenerateRequest()
    data class CalendarRequest(
        val title: String,
        val location: String = "",
        val startTime: String = "",
        val endTime: String = ""
    ) : QrGenerateRequest()
    data class LocationRequest(val lat: Double, val lng: Double) : QrGenerateRequest()
    data class ClipboardRequest(val content: String) : QrGenerateRequest()
    data class WifiRequest(
        val ssid: String,
        val password: String = "",
        val security: String = "WPA",
        val hidden: Boolean = false
    ) : QrGenerateRequest()
}
