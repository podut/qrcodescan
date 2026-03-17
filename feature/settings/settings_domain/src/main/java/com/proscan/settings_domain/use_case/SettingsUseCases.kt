package com.proscan.settings_domain.use_case

data class SettingsUseCases(
    val getUserProfile: GetUserProfile,
    val updateSettings: UpdateSettings,
    val upgradeToPro: UpgradeToPro
)
