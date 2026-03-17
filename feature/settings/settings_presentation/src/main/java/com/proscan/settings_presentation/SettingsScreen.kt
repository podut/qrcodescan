package com.proscan.settings_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.settings_presentation.components.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Setări",
            style = MaterialTheme.typography.headlineMedium
        )

        state.userProfile?.let { profile ->
            ProfileCard(
                deviceId = profile.deviceId,
                isPro = profile.isPro,
                scanCount = profile.scanCount
            )

            if (!profile.isPro) {
                PremiumUpgradeCard(
                    onUpgradeClick = { viewModel.onEvent(SettingsEvent.UpgradeToPro) }
                )
            }

            val settings = profile.settings

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingsToggleItem(
                        iconRes = "notifications",
                        title = "Notificări Push",
                        subtitle = "Primește notificări la fiecare scanare",
                        checked = settings.notifications,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleNotifications(it)) }
                    )
                    HorizontalDivider()
                    SettingsToggleItem(
                        iconRes = "volume",
                        title = "Semnal Sonor",
                        subtitle = "Sunet la scanarea unui cod",
                        checked = settings.beep,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleBeep(it)) }
                    )
                    HorizontalDivider()
                    SettingsToggleItem(
                        iconRes = "vibration",
                        title = "Feedback Tactil",
                        subtitle = "Vibrație la scanarea unui cod",
                        checked = settings.vibrate,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleVibrate(it)) }
                    )
                    HorizontalDivider()
                    SettingsToggleItem(
                        iconRes = "copy",
                        title = "Copiere Inteligentă",
                        subtitle = "Copiază automat rezultatul scanat",
                        checked = settings.autoCopy,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleAutoCopy(it)) }
                    )
                    HorizontalDivider()
                    SettingsToggleItem(
                        iconRes = "security",
                        title = "Securitate Activă",
                        subtitle = "Verifică link-urile pentru malware",
                        checked = settings.secureMode,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleSecureMode(it)) }
                    )
                    HorizontalDivider()
                    SettingsToggleItem(
                        iconRes = "history",
                        title = "Salvare Istoric",
                        subtitle = "Păstrează istoricul scanărilor",
                        checked = settings.saveHistory,
                        onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleSaveHistory(it)) }
                    )
                }
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        Text(
            text = "ProScan 2.2.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
