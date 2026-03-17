package com.proscan.generator_presentation

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.core.domain.model.ScanResult
import com.proscan.core.domain.model.ScanSource
import com.proscan.core.domain.model.ScanType
import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.generator_domain.model.OutputFormat
import com.proscan.generator_domain.model.QrGenerateRequest
import com.proscan.generator_domain.use_case.GeneratorUseCases
import com.proscan.history_domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val useCases: GeneratorUseCases,
    private val historyRepository: HistoryRepository,
    private val preferences: ProScanPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(GeneratorState())
    val state: StateFlow<GeneratorState> = _state.asStateFlow()

    fun onEvent(event: GeneratorEvent) {
        when (event) {
            is GeneratorEvent.SelectType -> {
                _state.value = _state.value.copy(
                    selectedType = event.type,
                    generatedBitmap = null,
                    generatedContent = "",
                    barcodeInput = "",
                    error = null
                )
            }
            is GeneratorEvent.UpdateTextField -> updateField(event.field, event.value)
            is GeneratorEvent.Generate -> generateQr()
            is GeneratorEvent.Share -> shareQr()
            is GeneratorEvent.CopyToClipboard -> copyToClipboard()
            is GeneratorEvent.ClearGenerated -> {
                _state.value = _state.value.copy(generatedBitmap = null, generatedContent = "")
            }
            is GeneratorEvent.GetCurrentLocation -> getCurrentLocation()
            is GeneratorEvent.SaveToGallery -> saveToGallery()
        }
    }

    private fun updateField(field: String, value: String) {
        val updated = when (field) {
            "text" -> _state.value.copy(textInput = value)
            "url" -> _state.value.copy(urlInput = value)
            "phone" -> _state.value.copy(phoneInput = value)
            "emailTo" -> _state.value.copy(emailTo = value)
            "emailSubject" -> _state.value.copy(emailSubject = value)
            "emailBody" -> _state.value.copy(emailBody = value)
            "smsPhone" -> _state.value.copy(smsPhone = value)
            "smsMessage" -> _state.value.copy(smsMessage = value)
            "contactName" -> _state.value.copy(contactName = value)
            "contactPhone" -> _state.value.copy(contactPhone = value)
            "contactEmail" -> _state.value.copy(contactEmail = value)
            "contactOrg" -> _state.value.copy(contactOrg = value)
            "contactAddress" -> _state.value.copy(contactAddress = value)
            "calendarTitle" -> _state.value.copy(calendarTitle = value)
            "calendarLocation" -> _state.value.copy(calendarLocation = value)
            "calendarStart" -> _state.value.copy(calendarStart = value)
            "calendarEnd" -> _state.value.copy(calendarEnd = value)
            "locationLat" -> _state.value.copy(locationLat = value)
            "locationLng" -> _state.value.copy(locationLng = value)
            "clipboard" -> _state.value.copy(clipboardContent = value)
            "barcode" -> _state.value.copy(barcodeInput = value)
            else -> _state.value
        }
        _state.value = updated.copy(error = null)
    }

    private fun validateInput(s: GeneratorState): String? = when (s.selectedType) {
        GeneratorType.TEXT -> if (s.textInput.isBlank()) "Completați câmpul de text" else null
        GeneratorType.URL -> if (s.urlInput.isBlank()) "Completați URL-ul" else null
        GeneratorType.PHONE -> if (s.phoneInput.isBlank()) "Completați numărul de telefon" else null
        GeneratorType.EMAIL -> if (s.emailTo.isBlank()) "Completați adresa de email" else null
        GeneratorType.SMS -> if (s.smsPhone.isBlank()) "Completați numărul de telefon" else null
        GeneratorType.CONTACT -> if (s.contactName.isBlank()) "Completați cel puțin numele" else null
        GeneratorType.CALENDAR -> if (s.calendarTitle.isBlank()) "Completați titlul evenimentului" else null
        GeneratorType.LOCATION -> when {
            s.locationLat.isBlank() -> "Completați latitudinea"
            s.locationLng.isBlank() -> "Completați longitudinea"
            s.locationLat.toDoubleOrNull() == null -> "Latitudine invalidă"
            s.locationLng.toDoubleOrNull() == null -> "Longitudine invalidă"
            else -> null
        }
        GeneratorType.CLIPBOARD -> if (s.clipboardContent.isBlank()) "Completați conținutul" else null
        else -> if (s.barcodeInput.isBlank()) "Completați valoarea codului" else null
    }

    private fun generateQr() {
        val s = _state.value
        val error = validateInput(s)
        if (error != null) {
            _state.value = s.copy(error = error)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val outputFormat = s.selectedType.toOutputFormat()
                val request = when (s.selectedType) {
                    GeneratorType.TEXT -> QrGenerateRequest.TextRequest(s.textInput)
                    GeneratorType.URL -> QrGenerateRequest.UrlRequest(s.urlInput)
                    GeneratorType.PHONE -> QrGenerateRequest.PhoneRequest(s.phoneInput)
                    GeneratorType.EMAIL -> QrGenerateRequest.EmailRequest(s.emailTo, s.emailSubject, s.emailBody)
                    GeneratorType.SMS -> QrGenerateRequest.SmsRequest(s.smsPhone, s.smsMessage)
                    GeneratorType.CONTACT -> QrGenerateRequest.ContactRequest(s.contactName, s.contactPhone, s.contactEmail, s.contactOrg, s.contactAddress)
                    GeneratorType.CALENDAR -> QrGenerateRequest.CalendarRequest(s.calendarTitle, s.calendarLocation, s.calendarStart, s.calendarEnd)
                    GeneratorType.LOCATION -> {
                        val lat = s.locationLat.toDoubleOrNull() ?: 0.0
                        val lng = s.locationLng.toDoubleOrNull() ?: 0.0
                        QrGenerateRequest.LocationRequest(lat, lng)
                    }
                    GeneratorType.CLIPBOARD -> QrGenerateRequest.ClipboardRequest(s.clipboardContent)
                    else -> QrGenerateRequest.TextRequest(s.barcodeInput)
                }
                val content = useCases.buildQrContent(request)
                val is1D = outputFormat != OutputFormat.QR_CODE &&
                        outputFormat != OutputFormat.DATA_MATRIX &&
                        outputFormat != OutputFormat.AZTEC &&
                        outputFormat != OutputFormat.PDF_417
                val bitmap = if (is1D) {
                    useCases.generateQrBitmap(content, outputFormat, 800, 300)
                } else {
                    useCases.generateQrBitmap(content, outputFormat, 512, 512)
                }
                _state.value = _state.value.copy(
                    generatedBitmap = bitmap,
                    generatedContent = content,
                    isLoading = false
                )

                val deviceId = preferences.getDeviceId()
                val scanResult = ScanResult(
                    deviceId = deviceId,
                    type = s.selectedType.toScanType(),
                    content = content,
                    format = outputFormat.name,
                    source = ScanSource.GENERATED
                )
                historyRepository.insertScan(scanResult)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                if (location != null) {
                    _state.value = _state.value.copy(
                        locationLat = "%.6f".format(location.latitude),
                        locationLng = "%.6f".format(location.longitude),
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(error = "Nu s-a putut obține locația. Activați GPS-ul.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Eroare locație: ${e.message}")
            }
        }
    }

    private fun saveToGallery() {
        val bitmap = _state.value.generatedBitmap ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val filename = "ProScan_${System.currentTimeMillis()}.png"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ProScan")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    if (uri != null) {
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                        }
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        context.contentResolver.update(uri, values, null, null)
                    }
                } else {
                    val dir = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ProScan")
                    dir.mkdirs()
                    val file = java.io.File(dir, filename)
                    file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
                    android.media.MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                }
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Salvat în Galerie (Pictures/ProScan)", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Nu s-a putut salva: ${e.message}")
            }
        }
    }

    private fun copyToClipboard() {
        val content = _state.value.generatedContent
        if (content.isNotBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("QR Content", content))
        }
    }

    private fun shareQr() {
        val bitmap = _state.value.generatedBitmap ?: return
        val content = _state.value.generatedContent
        try {
            val shareDir = java.io.File(context.cacheDir, "qr_share").also { it.mkdirs() }
            val file = java.io.File(shareDir, "qr_code.png")
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context, "${context.packageName}.provider", file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Distribuie QR Code").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Distribuie QR").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private fun GeneratorType.toScanType(): ScanType = when (this) {
        GeneratorType.URL      -> ScanType.URL
        GeneratorType.PHONE    -> ScanType.PHONE
        GeneratorType.EMAIL    -> ScanType.EMAIL
        GeneratorType.SMS      -> ScanType.SMS
        GeneratorType.CONTACT  -> ScanType.CONTACT
        GeneratorType.CALENDAR -> ScanType.CALENDAR
        GeneratorType.LOCATION -> ScanType.LOCATION
        else                   -> ScanType.TEXT
    }

    private fun GeneratorType.toOutputFormat(): OutputFormat = when (this) {
        GeneratorType.EAN_13      -> OutputFormat.EAN_13
        GeneratorType.UPC_E       -> OutputFormat.UPC_E
        GeneratorType.UPC_A       -> OutputFormat.UPC_A
        GeneratorType.CODE_39     -> OutputFormat.CODE_39
        GeneratorType.CODE_93     -> OutputFormat.CODE_93
        GeneratorType.CODE_128    -> OutputFormat.CODE_128
        GeneratorType.ITF         -> OutputFormat.ITF
        GeneratorType.PDF_417     -> OutputFormat.PDF_417
        GeneratorType.CODABAR     -> OutputFormat.CODABAR
        GeneratorType.DATA_MATRIX -> OutputFormat.DATA_MATRIX
        GeneratorType.AZTEC       -> OutputFormat.AZTEC
        else                      -> OutputFormat.QR_CODE
    }
}
