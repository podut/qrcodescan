package com.proscan.core.domain.preferences

import com.proscan.core.domain.model.UserProfile
import com.proscan.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface ProScanPreferences {
    fun getDeviceId(): String
    fun getUserProfileFlow(): Flow<UserProfile>
    suspend fun getUserProfile(): UserProfile
    suspend fun saveSettings(settings: UserSettings)
    suspend fun upgradeToPro()
    suspend fun incrementScanCount()
    suspend fun saveUserProfile(profile: UserProfile)
}
