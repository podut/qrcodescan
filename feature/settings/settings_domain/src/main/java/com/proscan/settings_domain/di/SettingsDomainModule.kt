package com.proscan.settings_domain.di

import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.settings_domain.use_case.GetUserProfile
import com.proscan.settings_domain.use_case.SettingsUseCases
import com.proscan.settings_domain.use_case.UpdateSettings
import com.proscan.settings_domain.use_case.UpgradeToPro
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsDomainModule {

    @Provides
    @Singleton
    fun provideSettingsUseCases(
        preferences: ProScanPreferences
    ): SettingsUseCases = SettingsUseCases(
        getUserProfile = GetUserProfile(preferences),
        updateSettings = UpdateSettings(preferences),
        upgradeToPro = UpgradeToPro(preferences)
    )
}
