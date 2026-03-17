package com.proscan.scanner_data.repository

import com.proscan.core.domain.model.ScanResult
import com.proscan.history_domain.repository.HistoryRepository
import com.proscan.scanner_domain.repository.ScannerRepository
import javax.inject.Inject

class ScannerRepositoryImpl @Inject constructor(
    private val historyRepository: HistoryRepository
) : ScannerRepository {

    override suspend fun saveScan(scan: ScanResult): String {
        historyRepository.insertScan(scan)
        return scan.id
    }
}
