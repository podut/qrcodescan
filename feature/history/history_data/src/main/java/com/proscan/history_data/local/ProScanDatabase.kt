package com.proscan.history_data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proscan.history_data.local.entity.ScanHistoryEntity

@Database(
    entities = [ScanHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProScanDatabase : RoomDatabase() {
    abstract val scanHistoryDao: ScanHistoryDao

    companion object {
        const val DATABASE_NAME = "proscan_db"
    }
}
