package com.proscan.generator_presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun EmailForm(
    to: String, subject: String, body: String,
    onToChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onBodyChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = to, onValueChange = onToChange, label = { Text("Către") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = subject, onValueChange = onSubjectChange, label = { Text("Subiect") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = body, onValueChange = onBodyChange, label = { Text("Mesaj") }, minLines = 2, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun SmsForm(phone: String, message: String, onPhoneChange: (String) -> Unit, onMessageChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Număr de telefon") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = message, onValueChange = onMessageChange, label = { Text("Mesaj") }, minLines = 2, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ContactForm(
    name: String, phone: String, email: String, org: String, address: String,
    onNameChange: (String) -> Unit, onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit, onOrgChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nume") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = org, onValueChange = onOrgChange, label = { Text("Organizație") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Adresă") }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun CalendarForm(
    title: String, location: String, start: String, end: String,
    onTitleChange: (String) -> Unit, onLocationChange: (String) -> Unit,
    onStartChange: (String) -> Unit, onEndChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = title, onValueChange = onTitleChange, label = { Text("Titlu eveniment") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = location, onValueChange = onLocationChange, label = { Text("Locație") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = start, onValueChange = onStartChange, label = { Text("Data început (YYYYMMDDTHHMMSS)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = end, onValueChange = onEndChange, label = { Text("Data sfârșit (YYYYMMDDTHHMMSS)") }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun LocationForm(lat: String, lng: String, onLatChange: (String) -> Unit, onLngChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = lat, onValueChange = onLatChange, label = { Text("Latitudine") }, placeholder = { Text("44.4268") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = lng, onValueChange = onLngChange, label = { Text("Longitudine") }, placeholder = { Text("26.1025") }, modifier = Modifier.fillMaxWidth())
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

@Composable
fun BarcodeForm(value: String, onValueChange: (String) -> Unit, hint: String, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(hint) },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = hint,
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
