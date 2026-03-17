package com.proscan.generator_presentation

import android.graphics.Bitmap
import com.proscan.core.domain.model.ScanType

data class GeneratorState(
    val selectedType: GeneratorType = GeneratorType.TEXT,
    val textInput: String = "",
    val urlInput: String = "",
    val phoneInput: String = "",
    val emailTo: String = "",
    val emailSubject: String = "",
    val emailBody: String = "",
    val smsPhone: String = "",
    val smsMessage: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val contactOrg: String = "",
    val contactAddress: String = "",
    val calendarTitle: String = "",
    val calendarLocation: String = "",
    val calendarStart: String = "",
    val calendarEnd: String = "",
    val locationLat: String = "",
    val locationLng: String = "",
    val clipboardContent: String = "",
    val generatedBitmap: Bitmap? = null,
    val generatedContent: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class GeneratorType(val label: String) {
    TEXT("Text"),
    URL("URL"),
    PHONE("Telefon"),
    EMAIL("Email"),
    SMS("SMS"),
    CONTACT("Contact"),
    CALENDAR("Calendar"),
    LOCATION("Locație"),
    CLIPBOARD("Clipboard")
}
