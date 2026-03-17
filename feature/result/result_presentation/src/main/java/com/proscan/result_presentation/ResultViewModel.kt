package com.proscan.result_presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.core.domain.util.UiEvent
import com.proscan.history_domain.repository.HistoryRepository
import com.proscan.result_domain.model.ScanAction
import com.proscan.result_domain.use_case.DetectScanActions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
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
                _state.value = ResultState(
                    scanResult = scan,
                    actions = actions,
                    isLoading = false
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
        if (action is ScanAction.OpenUrl && isPaymentUrl(action.url)) {
            _state.value = _state.value.copy(
                showPaymentWarning = true,
                pendingAction = action,
                warningDomain = extractDomain(action.url)
            )
        } else {
            launchAction(action)
        }
    }

    private fun executeAction(action: ScanAction) {
        val intent = when (action) {
            is ScanAction.OpenUrl -> Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
            is ScanAction.Dial -> Intent(Intent.ACTION_DIAL, Uri.parse(action.phone))
            is ScanAction.SendEmail -> Intent(Intent.ACTION_SENDTO, Uri.parse(action.mailto))
            is ScanAction.SendSms -> Intent(Intent.ACTION_SENDTO, Uri.parse(action.sms))
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

    private fun copyContent() {
        val content = _state.value.scanResult?.content ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("QR Content", content))
    }

    private fun shareContent() {
        val content = _state.value.scanResult?.content ?: return
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
