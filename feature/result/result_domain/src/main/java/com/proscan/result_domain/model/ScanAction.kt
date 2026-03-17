package com.proscan.result_domain.model

sealed class ScanAction(val label: String) {
    data class OpenUrl(val url: String) : ScanAction("Deschide Link")
    data class Dial(val phone: String) : ScanAction("Apelează")
    data class SendEmail(val mailto: String) : ScanAction("Trimite Email")
    data class SendSms(val sms: String) : ScanAction("Trimite SMS")
    data class OpenMap(val geo: String) : ScanAction("Deschide Hartă")
    data class SaveContact(val vcard: String) : ScanAction("Salvează în Agendă")
    data class AddCalendar(val url: String) : ScanAction("Adaugă în Calendar")
}
