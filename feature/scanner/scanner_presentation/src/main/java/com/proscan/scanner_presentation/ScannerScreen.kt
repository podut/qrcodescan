package com.proscan.scanner_presentation

import android.Manifest
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.proscan.core.domain.util.UiEvent
import com.proscan.scanner_presentation.components.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onNavigate: (String) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA) { granted ->
        if (granted) viewModel.onEvent(ScannerEvent.PermissionGranted)
        else viewModel.onEvent(ScannerEvent.PermissionDenied)
    }

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            viewModel.onEvent(ScannerEvent.PermissionGranted)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.NavigateUp -> onNavigateUp()
                else -> Unit
            }
        }
    }

    var camera by remember { mutableStateOf<Camera?>(null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    // Hold a reference to the PreviewView so we can rebind on flip
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    // Single camera binding — evită double-bind la schimbarea previewView
    LaunchedEffect(Unit) {
        val provider = suspendCancellableCoroutine<ProcessCameraProvider> { cont ->
            cameraProviderFuture.addListener(
                { cont.resume(cameraProviderFuture.get()) },
                ContextCompat.getMainExecutor(context)
            )
        }

        snapshotFlow { state.facingFront to previewView }
            .distinctUntilChanged()
            .collectLatest { (facingFront, pv) ->
                pv ?: return@collectLatest
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = pv.surfaceProvider
                }
                val cameraSelector = if (facingFront) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            processImage(imageProxy, barcodeScanner) { value, format ->
                                viewModel.onEvent(ScannerEvent.CodeScanned(value, format))
                            }
                        }
                    }
                try {
                    provider.unbindAll()
                    camera = provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

    // Flash — pornit o singură dată per cameră, rămâne activ
    LaunchedEffect(camera) {
        val cam = camera ?: return@LaunchedEffect
        snapshotFlow { state.flashEnabled }
            .distinctUntilChanged()
            .collectLatest { enabled ->
                try {
                    if (cam.cameraInfo.hasFlashUnit()) cam.cameraControl.enableTorch(enabled)
                } catch (_: Exception) {}
            }
    }

    // Zoom
    LaunchedEffect(camera) {
        val cam = camera ?: return@LaunchedEffect
        snapshotFlow { state.zoomLevel }
            .distinctUntilChanged()
            .collectLatest { level ->
                try { cam.cameraControl.setLinearZoom(level) } catch (_: Exception) {}
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.hasPermission) {
            // Camera Preview — factory just creates the view; binding is in LaunchedEffect above
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { pv ->
                        previewView = pv
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Scanner overlay — reserve space for zoom slider + buttons + nav bar
            ScannerOverlay(
                isBatchMode = state.isBatchMode,
                batchScanCount = state.batchScanCount,
                lastScanned = state.lastScanned,
                bottomReservedDp = 180
            )

            // Logo header with Lottie animation
            ProScanLogoBar(modifier = Modifier.align(Alignment.TopStart))

            // Controls
            ScannerControls(
                flashEnabled = state.flashEnabled,
                isBatchMode = state.isBatchMode,
                zoomLevel = state.zoomLevel,
                onFlashToggle = { viewModel.onEvent(ScannerEvent.ToggleFlash) },
                onBatchToggle = { viewModel.onEvent(ScannerEvent.ToggleBatch) },
                onFlipCamera = { viewModel.onEvent(ScannerEvent.FlipCamera) },
                onClose = { viewModel.onEvent(ScannerEvent.Close) },
                onZoomChange = { viewModel.onEvent(ScannerEvent.SetZoom(it)) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Batch mode chip
            if (state.isBatchMode && state.lastScanned != null) {
                BatchModeChip(
                    lastScanned = state.lastScanned!!,
                    scanCount = state.batchScanCount,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                )
            }
        } else {
            PermissionRequest(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            barcodeScanner.close()
        }
    }
}

@Composable
private fun ProScanLogoBar(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.qr_scan)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Pro",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Light
                )
            )
            Text(
                text = "Scan",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF818CF8), Color(0xFFA78BFA))
                    ),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImage(
    imageProxy: ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onResult: (String, String) -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.let { barcode ->
                val value = barcode.rawValue ?: return@addOnSuccessListener
                val format = when (barcode.format) {
                    Barcode.FORMAT_QR_CODE -> "QR_CODE"
                    Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
                    Barcode.FORMAT_PDF417 -> "PDF_417"
                    Barcode.FORMAT_AZTEC -> "AZTEC"
                    else -> "UNKNOWN"
                }
                onResult(value, format)
            }
        }
        .addOnCompleteListener { imageProxy.close() }
}
