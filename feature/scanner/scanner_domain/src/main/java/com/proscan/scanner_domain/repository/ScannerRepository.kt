package com.proscan.scanner_domain.repository

import com.proscan.core.domain.model.ScanResult

interface ScannerRepository {
    suspend fun saveScan(scan: ScanResult): String // returns scan id
}
