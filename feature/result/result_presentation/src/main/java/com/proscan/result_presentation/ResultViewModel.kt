package com.proscan.result_presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.core.domain.feature_flags.FeatureFlag
import com.proscan.core.domain.feature_flags.FeatureFlagRepository
import com.proscan.core.domain.feature_flags.FeatureUsageTracker
import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.core.domain.util.UiEvent
import com.proscan.core.util.SoundManager
import com.proscan.history_domain.repository.HistoryRepository
import com.proscan.result_domain.model.ScanAction
import com.proscan.result_domain.use_case.DetectScanActions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private val PAYMENT_KEYWORDS = listOf(
    "pay", "payment", "checkout", "plata", "parcare", "parking",
    "card", "secure", "verify", "bank", "stripe", "paypal", "revolut",
    "netopia", "euplatesc", "mobilpay", "order", "purchase", "billing"
)

private fun isPaymentUrl(url: String): Boolean {
    val lower = url.lowercase()
    return PAYMENT_KEYWORDS.any { lower.contains(it) }
}

private fun extractDomain(url: String): String = try {
    Uri.parse(url).host?.removePrefix("www.") ?: url
} catch (e: Exception) { url }

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val detectScanActions: DetectScanActions,
    private val soundManager: SoundManager,
    private val preferences: ProScanPreferences,
    @ApplicationContext private val context: Context,
    private val featureFlagRepository: FeatureFlagRepository,
    private val featureUsageTracker: FeatureUsageTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var beepEnabled = true

    init {
        preferences.getUserProfileFlow()
            .onEach { beepEnabled = it.settings.beep }
            .launchIn(viewModelScope)
        val scanId = savedStateHandle.get<String>("scanId")
        if (scanId != null) {
            loadScan(scanId)
        }
    }

    private fun loadScan(id: String) {
        viewModelScope.launch {
            val scan = historyRepository.getScanById(id)
            if (scan != null) {
                val actions = detectScanActions(scan)
                val config = featureFlagRepository.getConfig()
                val qrBitmap = withContext(Dispatchers.IO) { generateQrBitmap(scan.content) }
                val securityWarnings = if (scan.type == com.proscan.core.domain.model.ScanType.URL) {
                    analyzeUrlSecurity(scan.content)
                } else emptyList()
                _state.value = ResultState(
                    scanResult = scan,
                    qrBitmap = qrBitmap,
                    actions = actions,
                    isLoading = false,
                    domainHighlightEnabled = config.domainHighlight,
                    urlSecurityWarnings = securityWarnings
                )
            } else {
                _state.value = _state.value.copy(isLoading = false, error = "Scan not found")
            }
        }
    }

    fun onEvent(event: ResultEvent) {
        when (event) {
            is ResultEvent.ExecuteAction -> checkAndExecuteAction(event.action)
            is ResultEvent.ConfirmPendingAction -> {
                val pending = _state.value.pendingAction
                _state.value = _state.value.copy(showPaymentWarning = false, pendingAction = null, warningDomain = "")
                if (pending != null) launchAction(pending)
            }
            is ResultEvent.DismissWarning -> {
                _state.value = _state.value.copy(showPaymentWarning = false, pendingAction = null, warningDomain = "")
            }
            is ResultEvent.CopyToClipboard -> copyContent()
            is ResultEvent.Share -> shareContent()
            is ResultEvent.NavigateBack -> viewModelScope.launch { _uiEvent.send(UiEvent.NavigateUp) }
        }
    }

    private fun checkAndExecuteAction(action: ScanAction) {
        val config = featureFlagRepository.getConfig()
        if (config.paymentWarningDialog && action is ScanAction.OpenUrl && isPaymentUrl(action.url)) {
            _state.value = _state.value.copy(
                showPaymentWarning = true,
                pendingAction = action,
                warningDomain = extractDomain(action.url)
            )
            if (beepEnabled) soundManager.play(R.raw.sound_error)
            viewModelScope.launch { featureUsageTracker.track(FeatureFlag.PAYMENT_WARNING_DIALOG) }
        } else {
            launchAction(action)
        }
    }

    private fun executeAction(action: ScanAction) {
        if (action is ScanAction.OpenUrl) {
            viewModelScope.launch { featureUsageTracker.track(FeatureFlag.DOMAIN_HIGHLIGHT) }
        }
        val intent = when (action) {
            is ScanAction.OpenUrl -> Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
            is ScanAction.Dial -> Intent(Intent.ACTION_DIAL, Uri.parse(action.phone))
            is ScanAction.SendEmail -> Intent(Intent.ACTION_SENDTO, Uri.parse(action.mailto))
            is ScanAction.SendSms -> {
                val raw = action.sms
                val withoutScheme = raw.removePrefix("smsto:").removePrefix("SMSTO:")
                    .removePrefix("sms:").removePrefix("SMS:")
                val colonIdx = withoutScheme.indexOf(':')
                val phone = if (colonIdx >= 0) withoutScheme.substring(0, colonIdx) else withoutScheme
                val body = if (colonIdx >= 0) withoutScheme.substring(colonIdx + 1) else ""
                Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${phone.trim()}")).apply {
                    if (body.isNotBlank()) putExtra("sms_body", body.trim())
                }
            }
            is ScanAction.OpenMap -> Intent(Intent.ACTION_VIEW, Uri.parse(action.geo))
            is ScanAction.AddCalendar -> Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
            is ScanAction.SaveContact -> {
                // Write vCard to file and open
                Intent(Intent.ACTION_VIEW).apply {
                    type = "text/x-vcard"
                    putExtra(Intent.EXTRA_TEXT, action.vcard)
                }
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { context.startActivity(intent) } catch (e: Exception) { /* ignore */ }
    }

    private fun launchAction(action: ScanAction) = executeAction(action)

    private fun generateQrBitmap(content: String): Bitmap? = try {
        val size = 400
        val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            for (x in 0 until size) for (y in 0 until size) {
                setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    } catch (_: Exception) { null }

    private fun copyContent() {
        val content = _state.value.scanResult?.content ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("QR Content", content))
    }

    private fun shareContent() {
        val content = _state.value.scanResult?.content ?: return
        val bitmap = _state.value.qrBitmap
        try {
            if (bitmap != null) {
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
            } else {
                throw Exception("no bitmap")
            }
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Distribuie").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
