package com.proscan.settings_presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proscan.core.domain.model.UserSettings
import com.proscan.settings_domain.use_case.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCases: SettingsUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        useCases.getUserProfile()
            .onEach { profile ->
                _state.value = _state.value.copy(userProfile = profile, isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SettingsEvent) {
        val currentSettings = _state.value.userProfile?.settings ?: return
        when (event) {
            is SettingsEvent.ToggleVibrate -> updateSettings(currentSettings.copy(vibrate = event.enabled))
            is SettingsEvent.ToggleBeep -> updateSettings(currentSettings.copy(beep = event.enabled))
            is SettingsEvent.ToggleAutoCopy -> updateSettings(currentSettings.copy(autoCopy = event.enabled))
            is SettingsEvent.ToggleSaveHistory -> updateSettings(currentSettings.copy(saveHistory = event.enabled))
            is SettingsEvent.ToggleSecureMode -> updateSettings(currentSettings.copy(secureMode = event.enabled))
            is SettingsEvent.ToggleNotifications -> updateSettings(currentSettings.copy(notifications = event.enabled))
            is SettingsEvent.UpgradeToPro -> {
                viewModelScope.launch { useCases.upgradeToPro() }
            }
        }
    }

    private fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            useCases.updateSettings(settings)
        }
    }
}
