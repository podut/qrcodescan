package com.proscan.settings_presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.core.domain.model.AppTheme
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

            // ── Aspect (Theme + Dark Mode) ─────────────────────────────────
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Aspect",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Dark mode row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = if (settings.isDarkMode) "Mod întunecat" else "Mod luminos",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Schimbă aspectul interfeței",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = settings.isDarkMode,
                            onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleDarkMode(it)) },
                            colors = SwitchDefaults.colors(
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    HorizontalDivider()

                    // Theme color picker
                    Text(
                        text = "Culoare temă",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AppTheme.entries.forEach { theme ->
                            ThemeCircle(
                                theme = theme,
                                isSelected = settings.appTheme == theme,
                                onClick = { viewModel.onEvent(SettingsEvent.SelectTheme(theme)) }
                            )
                        }
                    }
                }
            }

            // ── General settings ───────────────────────────────────────────
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

@Composable
private fun ThemeCircle(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(theme.colorArgb))
                .then(
                    if (isSelected)
                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    else
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = theme.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
