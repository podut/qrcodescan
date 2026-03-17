package com.proscan.history_data.mapper

import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanSource
import com.proscan.core.domain.model.ScanType
import com.proscan.history_data.local.entity.ScanHistoryEntity

fun ScanHistoryEntity.toScanResult(): ScanResult {
    return ScanResult(
        id = id,
        deviceId = deviceId,
        type = try { ScanType.valueOf(type) } catch (e: Exception) { ScanType.UNKNOWN },
        content = content,
        format = format,
        source = source,
        createdAt = createdAt,
        isPinned = isPinned == 1,
        isSynced = isSynced == 1
    )
}

fun ScanResult.toEntity(): ScanHistoryEntity {
    return ScanHistoryEntity(
        id = id,
        deviceId = deviceId,
        type = type.name,
        content = content,
        format = format,
        source = source,
        createdAt = createdAt,
        isPinned = if (isPinned) 1 else 0,
        isSynced = if (isSynced) 1 else 0
    )
}
