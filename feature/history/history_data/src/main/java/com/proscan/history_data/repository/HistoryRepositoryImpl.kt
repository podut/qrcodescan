package com.proscan.history_data.repository

import com.proscan.core.domain.model.ScanResult
import com.proscan.history_data.local.ScanHistoryDao
import com.proscan.history_data.mapper.toEntity
import com.proscan.history_data.mapper.toScanResult
import com.proscan.history_domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dao: ScanHistoryDao
) : HistoryRepository {

    override fun getAllScans(): Flow<List<ScanResult>> {
        return dao.getAllScans().map { entities ->
            entities.map { it.toScanResult() }
        }
    }

    override suspend fun insertScan(scan: ScanResult) {
        dao.insertScan(scan.toEntity())
    }

    override suspend fun deleteScan(id: String) {
        dao.deleteScan(id)
    }

    override suspend fun togglePin(id: String) {
        dao.togglePin(id)
    }

    override suspend fun getScanById(id: String): ScanResult? {
        return dao.getScanById(id)?.toScanResult()
    }
}
