package com.proscan.settings_presentation

import com.proscan.core.domain.model.UserProfile

data class SettingsState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = true
)
