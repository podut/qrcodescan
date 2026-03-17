package com.proscan.generator_domain.use_case

import com.proscan.generator_domain.model.QrGenerateRequest

class BuildQrContent {
    operator fun invoke(request: QrGenerateRequest): String {
        return when (request) {
            is QrGenerateRequest.TextRequest -> request.text
            is QrGenerateRequest.UrlRequest -> {
                val url = request.url.trim()
                if (url.startsWith("http://") || url.startsWith("https://")) url
                else "https://$url"
            }
            is QrGenerateRequest.PhoneRequest -> "tel:${request.phone}"
            is QrGenerateRequest.EmailRequest -> {
                val sb = StringBuilder("mailto:${request.to}")
                val params = mutableListOf<String>()
                if (request.subject.isNotBlank()) params.add("subject=${request.subject}")
                if (request.body.isNotBlank()) params.add("body=${request.body}")
                if (params.isNotEmpty()) sb.append("?${params.joinToString("&")}")
                sb.toString()
            }
            is QrGenerateRequest.SmsRequest -> {
                val sms = "smsto:${request.phone}"
                if (request.message.isNotBlank()) "$sms:${request.message}" else sms
            }
            is QrGenerateRequest.ContactRequest -> buildString {
                appendLine("BEGIN:VCARD")
                appendLine("VERSION:3.0")
                appendLine("FN:${request.name}")
                if (request.phone.isNotBlank()) appendLine("TEL:${request.phone}")
                if (request.email.isNotBlank()) appendLine("EMAIL:${request.email}")
                if (request.org.isNotBlank()) appendLine("ORG:${request.org}")
                if (request.address.isNotBlank()) appendLine("ADR:${request.address}")
                append("END:VCARD")
            }
            is QrGenerateRequest.CalendarRequest -> buildString {
                append("https://calendar.google.com/calendar/render?action=TEMPLATE")
                append("&text=${request.title}")
                if (request.location.isNotBlank()) append("&location=${request.location}")
                if (request.startTime.isNotBlank()) append("&dates=${request.startTime}/${request.endTime}")
            }
            is QrGenerateRequest.LocationRequest -> "geo:${request.lat},${request.lng}"
            is QrGenerateRequest.ClipboardRequest -> request.content
        }
    }
}
