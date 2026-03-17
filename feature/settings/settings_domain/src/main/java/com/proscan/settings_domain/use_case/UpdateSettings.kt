package com.proscan.settings_domain.use_case

import com.proscan.core.domain.model.UserSettings
import com.proscan.core.domain.preferences.ProScanPreferences

class UpdateSettings(
    private val preferences: ProScanPreferences
) {
    suspend operator fun invoke(settings: UserSettings) {
        preferences.saveSettings(settings)
    }
}
