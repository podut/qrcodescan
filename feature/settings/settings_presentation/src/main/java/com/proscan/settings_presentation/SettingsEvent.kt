package com.proscan.settings_presentation

import com.proscan.core.domain.model.AppTheme

sealed class SettingsEvent {
    data class ToggleVibrate(val enabled: Boolean) : SettingsEvent()
    data class ToggleBeep(val enabled: Boolean) : SettingsEvent()
    data class ToggleAutoCopy(val enabled: Boolean) : SettingsEvent()
    data class ToggleSaveHistory(val enabled: Boolean) : SettingsEvent()
    data class ToggleSecureMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent()
    data class SelectTheme(val theme: AppTheme) : SettingsEvent()
    data class ToggleDarkMode(val enabled: Boolean) : SettingsEvent()
    object UpgradeToPro : SettingsEvent()
}
