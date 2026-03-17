package com.proscan.history_domain.use_case

import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanType

data class ScanStats(
    val totalCount: Int,
    val todayCount: Int,
    val countByType: Map<ScanType, Int>
)

class GetScanStats {
    operator fun invoke(scans: List<ScanResult>): ScanStats {
        val todayStart = getTodayStartMillis()
        return ScanStats(
            totalCount = scans.size,
            todayCount = scans.count { it.createdAt >= todayStart },
            countByType = scans.groupBy { it.type }.mapValues { it.value.size }
        )
    }

    private fun getTodayStartMillis(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
