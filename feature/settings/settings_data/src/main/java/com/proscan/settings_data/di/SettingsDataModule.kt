package com.proscan.settings_data.di

import com.proscan.core.data.preferences.DefaultProScanPreferences
import com.proscan.core.domain.preferences.ProScanPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {

    @Binds
    @Singleton
    abstract fun bindProScanPreferences(
        impl: DefaultProScanPreferences
    ): ProScanPreferences
}
