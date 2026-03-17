package com.proscan.settings_domain.use_case

import com.proscan.core.domain.model.UserProfile
import com.proscan.core.domain.preferences.ProScanPreferences
import kotlinx.coroutines.flow.Flow

class GetUserProfile(
    private val preferences: ProScanPreferences
) {
    operator fun invoke(): Flow<UserProfile> = preferences.getUserProfileFlow()
}
