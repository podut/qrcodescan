package com.proscan.history_data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey
    val id: String,
    val deviceId: String,
    val type: String,
    val content: String,
    val format: String,
    val createdAt: Long,
    val isPinned: Int, // 0 = false, 1 = true
    val isSynced: Int  // 0 = false, 1 = true
)
