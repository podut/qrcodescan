package com.proscan.history_domain.repository

import com.proscan.core.domain.model.ScanResult
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllScans(): Flow<List<ScanResult>>
    suspend fun insertScan(scan: ScanResult)
    suspend fun deleteScan(id: String)
    suspend fun togglePin(id: String)
    suspend fun getScanById(id: String): ScanResult?
}
