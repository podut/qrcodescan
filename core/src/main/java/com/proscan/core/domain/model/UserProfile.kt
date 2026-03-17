package com.proscan.core.domain.model

data class UserProfile(
    val deviceId: String,
    val isPro: Boolean = false,
    val scanCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val settings: UserSettings = UserSettings()
)
