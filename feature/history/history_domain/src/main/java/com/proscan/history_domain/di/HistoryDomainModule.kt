package com.proscan.history_domain.di

import com.proscan.history_domain.repository.HistoryRepository
import com.proscan.history_domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HistoryDomainModule {

    @Provides
    @Singleton
    fun provideHistoryUseCases(
        repository: HistoryRepository
    ): HistoryUseCases {
        return HistoryUseCases(
            getScanHistory = GetScanHistory(repository),
            deleteScan = DeleteScan(repository),
            togglePinScan = TogglePinScan(repository),
            searchScans = SearchScans(),
            getScanStats = GetScanStats(),
            exportToCSV = ExportToCSV()
        )
    }
}
