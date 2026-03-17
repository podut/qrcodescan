package com.proscan.settings_domain.use_case

import com.proscan.core.domain.preferences.ProScanPreferences

class UpgradeToPro(
    private val preferences: ProScanPreferences
) {
    suspend operator fun invoke() {
        preferences.upgradeToPro()
    }
}
