package com.proscan.result_domain.use_case

import com.proscan.core.domain.model.ScanResult
import com.proscan.result_domain.model.ScanAction

class DetectScanActions {
    operator fun invoke(scan: ScanResult): List<ScanAction> {
        val content = scan.content
        return buildList {
            when {
                content.startsWith("http://", ignoreCase = true) ||
                content.startsWith("https://", ignoreCase = true) -> {
                    add(ScanAction.OpenUrl(content))
                    if (content.contains("calendar.google.com", ignoreCase = true)) {
                        add(ScanAction.AddCalendar(content))
                    }
                }
                content.startsWith("tel:", ignoreCase = true) -> {
                    add(ScanAction.Dial(content))
                }
                content.startsWith("mailto:", ignoreCase = true) -> {
                    add(ScanAction.SendEmail(content))
                }
                content.startsWith("sms:", ignoreCase = true) ||
                content.startsWith("smsto:", ignoreCase = true) -> {
                    add(ScanAction.SendSms(content))
                }
                content.startsWith("BEGIN:VCARD", ignoreCase = true) -> {
                    add(ScanAction.SaveContact(content))
                }
                content.startsWith("geo:", ignoreCase = true) -> {
                    add(ScanAction.OpenMap(content))
                }
            }
        }
    }
}
