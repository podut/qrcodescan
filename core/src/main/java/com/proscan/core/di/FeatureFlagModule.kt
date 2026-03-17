package com.proscan.core.di

import com.proscan.core.data.feature_flags.DefaultFeatureFlagRepository
import com.proscan.core.data.feature_flags.DefaultFeatureUsageTracker
import com.proscan.core.domain.feature_flags.FeatureFlagRepository
import com.proscan.core.domain.feature_flags.FeatureUsageTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureFlagModule {

    @Binds
    @Singleton
    abstract fun bindFeatureFlagRepository(
        impl: DefaultFeatureFlagRepository
    ): FeatureFlagRepository

    @Binds
    @Singleton
    abstract fun bindFeatureUsageTracker(
        impl: DefaultFeatureUsageTracker
    ): FeatureUsageTracker
}
