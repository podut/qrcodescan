package com.proscan.result_domain.di

import com.proscan.result_domain.use_case.DetectScanActions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResultDomainModule {

    @Provides
    @Singleton
    fun provideDetectScanActions(): DetectScanActions = DetectScanActions()
}
