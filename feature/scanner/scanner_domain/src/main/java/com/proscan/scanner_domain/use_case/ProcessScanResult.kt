package com.proscan.scanner_domain.use_case

import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanType
import java.util.UUID

class ProcessScanResult {
    operator fun invoke(rawValue: String, deviceId: String, format: String = "QR_CODE"): ScanResult {
        val type = detectType(rawValue)
        return ScanResult(
            id = UUID.randomUUID().toString(),
            deviceId = deviceId,
            type = type,
            content = rawValue,
            format = format,
            createdAt = System.currentTimeMillis(),
            isPinned = false,
            isSynced = false
        )
    }

    private fun detectType(content: String): ScanType {
        return when {
            content.startsWith("http://", ignoreCase = true) ||
            content.startsWith("https://", ignoreCase = true) -> ScanType.URL
            content.startsWith("tel:", ignoreCase = true) -> ScanType.PHONE
            content.startsWith("mailto:", ignoreCase = true) -> ScanType.EMAIL
            content.startsWith("sms:", ignoreCase = true) ||
            content.startsWith("smsto:", ignoreCase = true) -> ScanType.SMS
            content.startsWith("BEGIN:VCARD", ignoreCase = true) -> ScanType.CONTACT
            content.startsWith("BEGIN:VEVENT", ignoreCase = true) ||
            content.contains("calendar.google.com", ignoreCase = true) -> ScanType.CALENDAR
            content.startsWith("geo:", ignoreCase = true) -> ScanType.LOCATION
            content.startsWith("WIFI:", ignoreCase = true) -> ScanType.WIFI
            else -> ScanType.TEXT
        }
    }
}
