package com.proscan.generator_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TextForm(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Text") },
        placeholder = { Text("Introdu textul...") },
        minLines = 3,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun UrlForm(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("URL") },
        placeholder = { Text("https://example.com") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PhoneForm(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Număr de telefon") },
        placeholder = { Text("+40712345678") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EmailForm(
    to: String, subject: String, body: String,
    onToChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onPickContact: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = to,
                onValueChange = onToChange,
                label = { Text("Către") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onPickContact) {
                Icon(
                    Icons.Default.Contacts,
                    contentDescription = "Alege din contacte",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        OutlinedTextField(
            value = subject,
            onValueChange = onSubjectChange,
            label = { Text("Subiect") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            label = { Text("Mesaj") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SmsForm(
    phone: String,
    message: String,
    onPhoneChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onPickContact: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Număr de telefon") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onPickContact) {
                Icon(
                    Icons.Default.Contacts,
                    contentDescription = "Alege din contacte",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            label = { Text("Mesaj") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContactForm(
    name: String, phone: String, email: String, org: String, address: String,
    onNameChange: (String) -> Unit, onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit, onOrgChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPickContact: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onPickContact,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Importă din agendă")
        }
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nume *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefon") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = org, onValueChange = onOrgChange, label = { Text("Organizație") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Adresă") }, modifier = Modifier.fillMaxWidth())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarForm(
    title: String, location: String, start: String, end: String,
    onTitleChange: (String) -> Unit, onLocationChange: (String) -> Unit,
    onPickStart: () -> Unit, onPickEnd: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Titlu eveniment *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Locație (opțional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        DatePickerButton(
            label = "Data început",
            selectedDate = start,
            onClick = onPickStart
        )
        DatePickerButton(
            label = "Data sfârșit",
            selectedDate = end,
            onClick = onPickEnd
        )
    }
}

@Composable
private fun DatePickerButton(label: String, selectedDate: String, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCalendarDate(selectedDate),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatCalendarDate(raw: String): String {
    if (raw.length < 8) return "Selectează data"
    return try {
        val parsed = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(raw.substring(0, 8)) ?: return "Selectează data"
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(parsed)
    } catch (e: Exception) { "Selectează data" }
}

@Composable
fun LocationForm(
    lat: String, lng: String,
    onLatChange: (String) -> Unit, onLngChange: (String) -> Unit,
    onGetCurrentLocation: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = onGetCurrentLocation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Folosește locația curentă")
        }
        OutlinedTextField(
            value = lat,
            onValueChange = onLatChange,
            label = { Text("Latitudine *") },
            placeholder = { Text("44.4268") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lng,
            onValueChange = onLngChange,
            label = { Text("Longitudine *") },
            placeholder = { Text("26.1025") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ClipboardForm(content: String, onContentChange: (String) -> Unit) {
    OutlinedTextField(
        value = content,
        onValueChange = onContentChange,
        label = { Text("Conținut clipboard") },
        minLines = 3,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiForm(
    ssid: String,
    password: String,
    security: String,
    hidden: Boolean,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSecurityChange: (String) -> Unit,
    onHiddenChange: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = ssid,
            onValueChange = onSsidChange,
            label = { Text("Nume rețea (SSID) *") },
            placeholder = { Text("MyWiFiNetwork") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Parolă") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Ascunde parola" else "Arată parola"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = when (security) {
                    "nopass" -> "Fără parolă"
                    "WEP" -> "WEP"
                    else -> "WPA/WPA2"
                },
                onValueChange = {},
                readOnly = true,
                label = { Text("Tip securitate") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("WPA/WPA2") },
                    onClick = { onSecurityChange("WPA"); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("WEP") },
                    onClick = { onSecurityChange("WEP"); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Fără parolă") },
                    onClick = { onSecurityChange("nopass"); expanded = false }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rețea ascunsă", style = MaterialTheme.typography.bodyMedium)
            Switch(checked = hidden, onCheckedChange = onHiddenChange)
        }
    }
}

@Composable
fun BarcodeForm(value: String, onValueChange: (String) -> Unit, hint: String, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(hint) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
