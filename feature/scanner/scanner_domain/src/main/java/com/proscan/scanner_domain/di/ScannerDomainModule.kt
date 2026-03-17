package com.proscan.scanner_domain.di

import com.proscan.scanner_domain.repository.ScannerRepository
import com.proscan.scanner_domain.use_case.ProcessScanResult
import com.proscan.scanner_domain.use_case.SaveScanToHistory
import com.proscan.scanner_domain.use_case.ScannerUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScannerDomainModule {

    @Provides
    @Singleton
    fun provideScannerUseCases(
        repository: ScannerRepository
    ): ScannerUseCases {
        return ScannerUseCases(
            processScanResult = ProcessScanResult(),
            saveScanToHistory = SaveScanToHistory(repository)
        )
    }
}
