package com.proscan.scanner_data.di

import com.proscan.scanner_data.repository.ScannerRepositoryImpl
import com.proscan.scanner_domain.repository.ScannerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScannerDataModule {

    @Binds
    @Singleton
    abstract fun bindScannerRepository(
        impl: ScannerRepositoryImpl
    ): ScannerRepository
}
