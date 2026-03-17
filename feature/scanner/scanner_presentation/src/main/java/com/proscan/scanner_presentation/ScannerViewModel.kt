package com.proscan.scanner_presentation

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.core.domain.util.UiEvent
import com.proscan.scanner_domain.use_case.ScannerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val scannerUseCases: ScannerUseCases,
    private val preferences: ProScanPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var cooldownJob: Job? = null
    private var isCooldown = false

    fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.ToggleFlash -> {
                _state.value = _state.value.copy(flashEnabled = !_state.value.flashEnabled)
            }
            is ScannerEvent.ToggleBatch -> {
                _state.value = _state.value.copy(
                    isBatchMode = !_state.value.isBatchMode,
                    batchScanCount = 0
                )
            }
            is ScannerEvent.FlipCamera -> {
                _state.value = _state.value.copy(facingFront = !_state.value.facingFront)
            }
            is ScannerEvent.PermissionGranted -> {
                _state.value = _state.value.copy(hasPermission = true)
            }
            is ScannerEvent.PermissionDenied -> {
                _state.value = _state.value.copy(hasPermission = false)
            }
            is ScannerEvent.CodeScanned -> {
                if (!isCooldown) {
                    handleScan(event.value, event.format)
                }
            }
            is ScannerEvent.Close -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateUp)
                }
            }
            is ScannerEvent.ResetCooldown -> {
                isCooldown = false
            }
            is ScannerEvent.SetZoom -> {
                _state.value = _state.value.copy(zoomLevel = event.level)
            }
        }
    }

    private fun handleScan(value: String, format: String) {
        viewModelScope.launch {
            val userProfile = preferences.getUserProfile()
            val deviceId = preferences.getDeviceId()

            // Vibrate if enabled
            if (userProfile.settings.vibrate) {
                vibrate()
            }

            // Process and save scan
            val scanResult = scannerUseCases.processScanResult(value, deviceId, format)
            if (userProfile.settings.saveHistory) {
                scannerUseCases.saveScanToHistory(scanResult)
            }

            if (_state.value.isBatchMode) {
                // Batch mode: update state and start cooldown
                _state.value = _state.value.copy(
                    lastScanned = value,
                    batchScanCount = _state.value.batchScanCount + 1
                )
                isCooldown = true
                cooldownJob?.cancel()
                cooldownJob = launch {
                    delay(2500L) // 2.5 second cooldown
                    isCooldown = false
                }
            } else {
                // Normal mode: navigate to result
                _uiEvent.send(UiEvent.Navigate("result/${scanResult.id}"))
            }
        }
    }

    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            }
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }
}
