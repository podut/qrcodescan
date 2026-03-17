package com.proscan.history_data.local

import androidx.room.*
import com.proscan.history_data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY isPinned DESC, createdAt DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE id = :id LIMIT 1")
    suspend fun getScanById(id: String): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteScan(id: String)

    @Query("UPDATE scan_history SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun togglePin(id: String)

    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getCount(): Int
}
