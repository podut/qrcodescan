package com.proscan.generator_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.generator_presentation.components.*

@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Generează QR",
            style = MaterialTheme.typography.headlineMedium
        )

        TypeSelector(
            selectedType = state.selectedType,
            onTypeSelected = { viewModel.onEvent(GeneratorEvent.SelectType(it)) }
        )

        // Form based on selected type
        when (state.selectedType) {
            GeneratorType.TEXT -> TextForm(
                value = state.textInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("text", it)) }
            )
            GeneratorType.URL -> UrlForm(
                value = state.urlInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("url", it)) }
            )
            GeneratorType.PHONE -> PhoneForm(
                value = state.phoneInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("phone", it)) }
            )
            GeneratorType.EMAIL -> EmailForm(
                to = state.emailTo,
                subject = state.emailSubject,
                body = state.emailBody,
                onToChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("emailTo", it)) },
                onSubjectChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("emailSubject", it)) },
                onBodyChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("emailBody", it)) }
            )
            GeneratorType.SMS -> SmsForm(
                phone = state.smsPhone,
                message = state.smsMessage,
                onPhoneChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("smsPhone", it)) },
                onMessageChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("smsMessage", it)) }
            )
            GeneratorType.CONTACT -> ContactForm(
                name = state.contactName,
                phone = state.contactPhone,
                email = state.contactEmail,
                org = state.contactOrg,
                address = state.contactAddress,
                onNameChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactName", it)) },
                onPhoneChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactPhone", it)) },
                onEmailChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactEmail", it)) },
                onOrgChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactOrg", it)) },
                onAddressChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactAddress", it)) }
            )
            GeneratorType.CALENDAR -> CalendarForm(
                title = state.calendarTitle,
                location = state.calendarLocation,
                start = state.calendarStart,
                end = state.calendarEnd,
                onTitleChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarTitle", it)) },
                onLocationChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarLocation", it)) },
                onStartChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarStart", it)) },
                onEndChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarEnd", it)) }
            )
            GeneratorType.LOCATION -> LocationForm(
                lat = state.locationLat,
                lng = state.locationLng,
                onLatChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("locationLat", it)) },
                onLngChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("locationLng", it)) }
            )
            GeneratorType.CLIPBOARD -> ClipboardForm(
                content = state.clipboardContent,
                onContentChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("clipboard", it)) }
            )
            GeneratorType.EAN_13 -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "EAN-13",
                hint = "12 cifre (ex: 590123412345)"
            )
            GeneratorType.UPC_E -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "UPC-E",
                hint = "6 cifre (ex: 012345)"
            )
            GeneratorType.UPC_A -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "UPC-A",
                hint = "11 cifre (ex: 01234567890)"
            )
            GeneratorType.CODE_39 -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "CODE 39",
                hint = "Litere mari, cifre, - . \$ / + % (ex: HELLO-123)"
            )
            GeneratorType.CODE_93 -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "CODE 93",
                hint = "Litere mari, cifre, caractere speciale"
            )
            GeneratorType.CODE_128 -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "CODE 128",
                hint = "Orice text ASCII (ex: ABC-123)"
            )
            GeneratorType.ITF -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "ITF",
                hint = "Număr par de cifre (ex: 12345678)"
            )
            GeneratorType.PDF_417 -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "PDF 417",
                hint = "Text sau date (ex: Text complet)"
            )
            GeneratorType.CODABAR -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "CODABAR",
                hint = "Cifre cu start/stop A/B/C/D (ex: A12345B)"
            )
            GeneratorType.DATA_MATRIX -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "Data Matrix",
                hint = "Orice text (ex: Hello World)"
            )
            GeneratorType.AZTEC -> BarcodeForm(
                value = state.barcodeInput,
                onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) },
                label = "Aztec",
                hint = "Orice text (ex: Hello World)"
            )
        }

        GenerateButton(
            isLoading = state.isLoading,
            onClick = { viewModel.onEvent(GeneratorEvent.Generate) }
        )

        if (state.generatedBitmap != null) {
            QrPreviewCard(
                bitmap = state.generatedBitmap!!,
                content = state.generatedContent,
                onShare = { viewModel.onEvent(GeneratorEvent.Share) },
                onCopy = { viewModel.onEvent(GeneratorEvent.CopyToClipboard) }
            )
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
