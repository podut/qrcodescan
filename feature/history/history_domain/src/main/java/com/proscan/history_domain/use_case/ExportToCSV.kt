package com.proscan.history_domain.use_case

import com.proscan.core.domain.model.ScanResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportToCSV {
    operator fun invoke(scans: List<ScanResult>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("ID,Type,Content,Format,CreatedAt,IsPinned")
        scans.forEach { scan ->
            val date = dateFormat.format(Date(scan.createdAt))
            val content = scan.content.replace("\"", "\"\"") // escape quotes
            sb.appendLine("\"${scan.id}\",\"${scan.type.name}\",\"$content\",\"${scan.format}\",\"$date\",\"${scan.isPinned}\"")
        }
        return sb.toString()
    }
}
