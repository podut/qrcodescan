package com.proscan.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.proscan.core.domain.model.UserProfile
import com.proscan.core.domain.model.UserSettings
import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.core.util.DeviceIdentity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "proscan_preferences")

@Singleton
class DefaultProScanPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) : ProScanPreferences {

    private object Keys {
        val IS_PRO = booleanPreferencesKey("is_pro")
        val SCAN_COUNT = intPreferencesKey("scan_count")
        val CREATED_AT = longPreferencesKey("created_at")
        // Settings
        val VIBRATE = booleanPreferencesKey("vibrate")
        val BEEP = booleanPreferencesKey("beep")
        val AUTO_COPY = booleanPreferencesKey("auto_copy")
        val SAVE_HISTORY = booleanPreferencesKey("save_history")
        val SECURE_MODE = booleanPreferencesKey("secure_mode")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
    }

    private val cachedDeviceId: String by lazy {
        DeviceIdentity.getOrCreateDeviceId(context)
    }

    override fun getDeviceId(): String = cachedDeviceId

    override fun getUserProfileFlow(): Flow<UserProfile> {
        return context.dataStore.data.map { prefs ->
            UserProfile(
                deviceId = cachedDeviceId,
                isPro = prefs[Keys.IS_PRO] ?: false,
                scanCount = prefs[Keys.SCAN_COUNT] ?: 0,
                createdAt = prefs[Keys.CREATED_AT] ?: System.currentTimeMillis(),
                settings = UserSettings(
                    vibrate = prefs[Keys.VIBRATE] ?: true,
                    beep = prefs[Keys.BEEP] ?: true,
                    autoCopy = prefs[Keys.AUTO_COPY] ?: false,
                    saveHistory = prefs[Keys.SAVE_HISTORY] ?: true,
                    secureMode = prefs[Keys.SECURE_MODE] ?: false,
                    notifications = prefs[Keys.NOTIFICATIONS] ?: false
                )
            )
        }
    }

    override suspend fun getUserProfile(): UserProfile {
        return getUserProfileFlow().first()
    }

    override suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VIBRATE] = settings.vibrate
            prefs[Keys.BEEP] = settings.beep
            prefs[Keys.AUTO_COPY] = settings.autoCopy
            prefs[Keys.SAVE_HISTORY] = settings.saveHistory
            prefs[Keys.SECURE_MODE] = settings.secureMode
            prefs[Keys.NOTIFICATIONS] = settings.notifications
        }
    }

    override suspend fun upgradeToPro() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_PRO] = true
        }
    }

    override suspend fun incrementScanCount() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.SCAN_COUNT] ?: 0
            prefs[Keys.SCAN_COUNT] = current + 1
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_PRO] = profile.isPro
            prefs[Keys.SCAN_COUNT] = profile.scanCount
            prefs[Keys.CREATED_AT] = profile.createdAt
            prefs[Keys.VIBRATE] = profile.settings.vibrate
            prefs[Keys.BEEP] = profile.settings.beep
            prefs[Keys.AUTO_COPY] = profile.settings.autoCopy
            prefs[Keys.SAVE_HISTORY] = profile.settings.saveHistory
            prefs[Keys.SECURE_MODE] = profile.settings.secureMode
            prefs[Keys.NOTIFICATIONS] = profile.settings.notifications
        }
    }
}
