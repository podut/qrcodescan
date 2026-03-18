package com.proscan.result_presentation

sealed class UrlSecurityWarning {
    object HttpOnly : UrlSecurityWarning()
    data class UrlShortener(val host: String) : UrlSecurityWarning()
    object SuspiciousDomain : UrlSecurityWarning()
}

private val SHORTENERS = setOf(
    "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "short.link",
    "rb.gy", "is.gd", "tiny.cc", "cutt.ly", "shorturl.at", "soo.gd",
    "url.rip", "bl.ink", "snip.ly", "clck.ru", "x.co", "q.gs",
    "v.gd", "linktr.ee", "lnkd.in"
)

fun analyzeUrlSecurity(url: String): List<UrlSecurityWarning> {
    val warnings = mutableListOf<UrlSecurityWarning>()
    val lower = url.trim().lowercase()

    // 1. HTTP without HTTPS
    if (lower.startsWith("http://")) {
        warnings.add(UrlSecurityWarning.HttpOnly)
    }

    // 2. URL shortener
    val host = extractHost(url)
    if (host != null && SHORTENERS.any { host == it || host.endsWith(".$it") }) {
        warnings.add(UrlSecurityWarning.UrlShortener(host))
    }

    // 3. Suspicious subdomain depth (4+ parts = at least 2 subdomains, e.g. login.secure.bank.com)
    if (host != null) {
        val parts = host.split(".")
        if (parts.size >= 4) {
            warnings.add(UrlSecurityWarning.SuspiciousDomain)
        }
    }

    return warnings
}

private fun extractHost(url: String): String? = try {
    android.net.Uri.parse(url).host?.lowercase()?.removePrefix("www.")
} catch (_: Exception) { null }
