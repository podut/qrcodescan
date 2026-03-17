package com.proscan.generator_domain.di

import com.proscan.generator_domain.use_case.BuildQrContent
import com.proscan.generator_domain.use_case.GenerateQrBitmap
import com.proscan.generator_domain.use_case.GeneratorUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeneratorDomainModule {

    @Provides
    @Singleton
    fun provideGeneratorUseCases(): GeneratorUseCases {
        return GeneratorUseCases(
            buildQrContent = BuildQrContent(),
            generateQrBitmap = GenerateQrBitmap()
        )
    }
}
