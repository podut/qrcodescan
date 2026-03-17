package com.proscan.generator_presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.proscan.generator_presentation.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    sharedText: String? = null,
    viewModel: GeneratorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(sharedText) {
        if (!sharedText.isNullOrBlank()) {
            viewModel.onEvent(GeneratorEvent.UpdateTextField("text", sharedText))
        }
    }

    // Scroll to bottom when QR is generated
    LaunchedEffect(state.generatedBitmap) {
        if (state.generatedBitmap != null) {
            kotlinx.coroutines.delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // --- Contact picker for Contact form ---
    val contactFormPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            val contactId = uri.lastPathSegment ?: return@rememberLauncherForActivityResult

            // Display name
            context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                "${ContactsContract.Contacts._ID} = ?",
                arrayOf(contactId), null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("contactName", c.getString(0).orEmpty().trim())
                )
            }

            // Phone
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId), null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("contactPhone", c.getString(0).orEmpty().trim())
                )
            }

            // Email
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                arrayOf(contactId), null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("contactEmail", c.getString(0).orEmpty().trim())
                )
            }

            // Organization
            context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Organization.COMPANY),
                "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE), null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("contactOrg", c.getString(0).orEmpty().trim())
                )
            }

            // Address
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS),
                "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID} = ?",
                arrayOf(contactId), null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("contactAddress", c.getString(0).orEmpty().trim())
                )
            }
        }
    }

    // --- READ_CONTACTS permission launcher ---
    val contactsPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            contactFormPickerLauncher.launch(
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            )
        }
    }

    // --- Contact picker for SMS phone ---
    val smsContactLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            context.contentResolver.query(
                uri, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("smsPhone", c.getString(0).trim())
                )
            }
        }
    }

    // --- Contact picker for Email "to" ---
    val emailContactLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            context.contentResolver.query(
                uri, arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS), null, null, null
            )?.use { c ->
                if (c.moveToFirst()) viewModel.onEvent(
                    GeneratorEvent.UpdateTextField("emailTo", c.getString(0).trim())
                )
            }
        }
    }

    // --- Location permission ---
    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onEvent(GeneratorEvent.GetCurrentLocation)
    }

    // --- Calendar date picker ---
    var datePickerFor by remember { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()

    if (datePickerFor != null) {
        DatePickerDialog(
            onDismissRequest = { datePickerFor = null },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(millis))
                        viewModel.onEvent(GeneratorEvent.UpdateTextField(datePickerFor!!, formatted))
                    }
                    datePickerFor = null
                }) { Text("Selectează") }
            },
            dismissButton = {
                TextButton(onClick = { datePickerFor = null }) { Text("Anulează") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                onBodyChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("emailBody", it)) },
                onPickContact = {
                    emailContactLauncher.launch(
                        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI)
                    )
                }
            )
            GeneratorType.SMS -> SmsForm(
                phone = state.smsPhone,
                message = state.smsMessage,
                onPhoneChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("smsPhone", it)) },
                onMessageChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("smsMessage", it)) },
                onPickContact = {
                    smsContactLauncher.launch(
                        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                    )
                }
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
                onAddressChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("contactAddress", it)) },
                onPickContact = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        contactFormPickerLauncher.launch(
                            Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                        )
                    } else {
                        contactsPermLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                }
            )
            GeneratorType.CALENDAR -> CalendarForm(
                title = state.calendarTitle,
                location = state.calendarLocation,
                start = state.calendarStart,
                end = state.calendarEnd,
                onTitleChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarTitle", it)) },
                onLocationChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("calendarLocation", it)) },
                onPickStart = { datePickerFor = "calendarStart" },
                onPickEnd = { datePickerFor = "calendarEnd" }
            )
            GeneratorType.LOCATION -> LocationForm(
                lat = state.locationLat,
                lng = state.locationLng,
                onLatChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("locationLat", it)) },
                onLngChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("locationLng", it)) },
                onGetCurrentLocation = {
                    val perm = Manifest.permission.ACCESS_FINE_LOCATION
                    if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.onEvent(GeneratorEvent.GetCurrentLocation)
                    } else {
                        locationPermLauncher.launch(perm)
                    }
                }
            )
            GeneratorType.CLIPBOARD -> ClipboardForm(
                content = state.clipboardContent,
                onContentChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("clipboard", it)) }
            )
            GeneratorType.EAN_13 -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "EAN-13", hint = "12 cifre (ex: 590123412345)")
            GeneratorType.UPC_E -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "UPC-E", hint = "6 cifre (ex: 012345)")
            GeneratorType.UPC_A -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "UPC-A", hint = "11 cifre (ex: 01234567890)")
            GeneratorType.CODE_39 -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "CODE 39", hint = "Litere mari, cifre, - . \$ / + % (ex: HELLO-123)")
            GeneratorType.CODE_93 -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "CODE 93", hint = "Litere mari, cifre, caractere speciale")
            GeneratorType.CODE_128 -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "CODE 128", hint = "Orice text ASCII (ex: ABC-123)")
            GeneratorType.ITF -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "ITF", hint = "Număr par de cifre (ex: 12345678)")
            GeneratorType.PDF_417 -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "PDF 417", hint = "Text sau date (ex: Text complet)")
            GeneratorType.CODABAR -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "CODABAR", hint = "Cifre cu start/stop A/B/C/D (ex: A12345B)")
            GeneratorType.DATA_MATRIX -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "Data Matrix", hint = "Orice text (ex: Hello World)")
            GeneratorType.AZTEC -> BarcodeForm(value = state.barcodeInput, onValueChange = { viewModel.onEvent(GeneratorEvent.UpdateTextField("barcode", it)) }, label = "Aztec", hint = "Orice text (ex: Hello World)")
        }

        GenerateButton(
            isLoading = state.isLoading,
            onClick = {
                focusManager.clearFocus()
                viewModel.onEvent(GeneratorEvent.Generate)
            }
        )

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // QR preview with smooth entrance animation
        AnimatedVisibility(
            visible = state.generatedBitmap != null,
            enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
        ) {
            state.generatedBitmap?.let { bitmap ->
                QrPreviewCard(
                    bitmap = bitmap,
                    content = state.generatedContent,
                    onShare = { viewModel.onEvent(GeneratorEvent.Share) },
                    onCopy = { viewModel.onEvent(GeneratorEvent.CopyToClipboard) },
                    onSave = { viewModel.onEvent(GeneratorEvent.SaveToGallery) }
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}
