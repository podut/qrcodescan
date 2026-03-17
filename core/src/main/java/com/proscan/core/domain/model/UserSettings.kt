package com.proscan.core.domain.model

data class UserSettings(
    val vibrate: Boolean = true,
    val beep: Boolean = true,
    val autoCopy: Boolean = false,
    val saveHistory: Boolean = true,
    val secureMode: Boolean = false,
    val notifications: Boolean = false
)
