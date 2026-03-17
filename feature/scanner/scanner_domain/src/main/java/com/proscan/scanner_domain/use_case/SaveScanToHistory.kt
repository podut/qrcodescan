package com.proscan.scanner_domain.use_case

import com.proscan.core.domain.model.ScanResult
import com.proscan.scanner_domain.repository.ScannerRepository

class SaveScanToHistory(
    private val repository: ScannerRepository
) {
    suspend operator fun invoke(scan: ScanResult): String {
        return repository.saveScan(scan)
    }
}
