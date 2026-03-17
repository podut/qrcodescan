package com.proscan.generator_presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.generator_domain.model.OutputFormat
import com.proscan.generator_domain.model.QrGenerateRequest
import com.proscan.generator_domain.use_case.GeneratorUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratorViewModel @Inject constructor(
    private val useCases: GeneratorUseCases,
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
                    barcodeInput = ""
                )
            }
            is GeneratorEvent.UpdateTextField -> {
                updateField(event.field, event.value)
            }
            is GeneratorEvent.Generate -> {
                generateQr()
            }
            is GeneratorEvent.Share -> {
                shareQr()
            }
            is GeneratorEvent.CopyToClipboard -> {
                copyToClipboard()
            }
            is GeneratorEvent.ClearGenerated -> {
                _state.value = _state.value.copy(generatedBitmap = null, generatedContent = "")
            }
        }
    }

    private fun updateField(field: String, value: String) {
        _state.value = when (field) {
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
    }

    private fun generateQr() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val s = _state.value
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
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

    private fun copyToClipboard() {
        val content = _state.value.generatedContent
        if (content.isNotBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("QR Content", content))
        }
    }

    private fun shareQr() {
        val content = _state.value.generatedContent
        if (content.isNotBlank()) {
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
}
