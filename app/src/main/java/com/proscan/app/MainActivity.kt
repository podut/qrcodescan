package com.proscan.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proscan.app.ui.ProScanApp
import com.proscan.core.domain.model.AppTheme
import com.proscan.core.domain.preferences.ProScanPreferences
import com.proscan.core_ui.theme.ProScanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

private val SplashBlue = Color(0xFF42A5F5)
private val SplashBlueDark = Color(0xFF1565C0)
private val SplashGray = Color(0xFF78909C)
private val SplashBg = Color(0xFFF8FAFF)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferences: ProScanPreferences

    private var sharedText = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedText.value = intent.takeIf { it.action == Intent.ACTION_SEND }
            ?.getStringExtra(Intent.EXTRA_TEXT)
        setContent {
            val profile by preferences.getUserProfileFlow().collectAsStateWithLifecycle(initialValue = null)
            val appTheme = profile?.settings?.appTheme ?: AppTheme.INDIGO
            val darkMode = profile?.settings?.isDarkMode ?: false

            ProScanTheme(appTheme = appTheme, darkMode = darkMode) {
                var splashDone by remember { mutableStateOf(false) }

                Crossfade(
                    targetState = splashDone,
                    animationSpec = tween(500),
                    label = "splash_crossfade"
                ) { done ->
                    if (done) {
                        ProScanApp(initialSharedText = sharedText.value)
                    } else {
                        SplashScreen(onFinished = { splashDone = true })
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        sharedText.value = intent.takeIf { it.action == Intent.ACTION_SEND }
            ?.getStringExtra(Intent.EXTRA_TEXT)
    }
}

@Composable
private fun SplashScreen(onFinished: () -> Unit) {
    // — Logo scale: spring bounce in
    val logoScale = remember { Animatable(0f) }
    // — Corner brackets draw: 0..1 progress
    val cornerProgress = remember { Animatable(0f) }
    // — Circle + checkmark appear
    val circleScale = remember { Animatable(0f) }
    // — Text slide + alpha
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(30f) }
    // — Tagline
    val tagAlpha = remember { Animatable(0f) }
    // — Whole screen fade out
    val screenAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // 1. Corners draw in
        cornerProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(420, easing = FastOutSlowInEasing)
        )
        // 2. Center logo bounces in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.45f, stiffness = 260f)
        )
        // 3. Circle with checkmark pops in
        circleScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 380f)
        )
        // 4. Brand text rises up
        launch { textAlpha.animateTo(1f, tween(350)) }
        textOffset.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
        // 5. Tagline fades in
        tagAlpha.animateTo(1f, tween(300))

        // Hold
        delay(900)

        // 6. Fade out entire splash
        screenAlpha.animateTo(0f, tween(400))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenAlpha.value)
            .background(SplashBg),
        contentAlignment = Alignment.Center
    ) {
        // Canvas: QR corners + center QR body + circle checkmark
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .scale(logoScale.value)
        ) {
            val w = size.width
            val h = size.height
            val sw = 5.dp.toPx()          // stroke width
            val cornerLen = w * 0.22f     // length of each bracket arm
            val cornerRad = w * 0.06f     // how far in corners are inset
            val cp = cornerProgress.value // 0..1

            // ── QR corner brackets (blue) ──────────────────────────────────
            val bracketColor = SplashBlue

            fun drawCorner(ox: Float, oy: Float, signX: Float, signY: Float) {
                // vertical arm
                drawLine(
                    color = bracketColor,
                    start = Offset(ox, oy + signY * cornerRad),
                    end = Offset(ox, oy + signY * (cornerRad + cornerLen * cp)),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
                // horizontal arm
                drawLine(
                    color = bracketColor,
                    start = Offset(ox + signX * cornerRad, oy),
                    end = Offset(ox + signX * (cornerRad + cornerLen * cp), oy),
                    strokeWidth = sw,
                    cap = StrokeCap.Round
                )
            }

            drawCorner(0f, 0f, 1f, 1f)           // top-left
            drawCorner(w, 0f, -1f, 1f)            // top-right
            drawCorner(0f, h, 1f, -1f)            // bottom-left
            drawCorner(w, h, -1f, -1f)            // bottom-right

            // ── QR inner "pixels" (gray, simplified 3×3 block pattern) ────
            val qScale = logoScale.value
            if (qScale > 0.3f) {
                val blockSize = w * 0.08f
                val gap = w * 0.035f
                val totalGrid = 3 * blockSize + 2 * gap
                val startX = (w - totalGrid) / 2f
                val startY = (h - totalGrid) / 2f

                // simple QR-like 3x3 grid pattern (skip center for circle)
                val pattern = listOf(
                    0 to 0, 2 to 0,
                    0 to 1,
                    0 to 2, 1 to 2, 2 to 2
                )
                pattern.forEach { (col, row) ->
                    val px = startX + col * (blockSize + gap)
                    val py = startY + row * (blockSize + gap)
                    drawRect(
                        color = SplashGray.copy(alpha = qScale),
                        topLeft = Offset(px, py),
                        size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                    )
                }
            }

            // ── Blue circle + white checkmark ─────────────────────────────
            val cs = circleScale.value
            if (cs > 0f) {
                val cx = w / 2f
                val cy = h / 2f
                val radius = w * 0.185f * cs

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(SplashBlue, SplashBlueDark),
                        center = Offset(cx - radius * 0.1f, cy - radius * 0.1f),
                        radius = radius * 1.2f
                    ),
                    radius = radius,
                    center = Offset(cx, cy)
                )

                // Checkmark
                val ckW = sw * 1.5f
                val arm1Start = Offset(cx - radius * 0.42f * cs, cy + radius * 0.02f * cs)
                val arm1End = Offset(cx - radius * 0.08f * cs, cy + radius * 0.38f * cs)
                val arm2End = Offset(cx + radius * 0.44f * cs, cy - radius * 0.26f * cs)

                drawLine(Color.White, arm1Start, arm1End, ckW, StrokeCap.Round)
                drawLine(Color.White, arm1End, arm2End, ckW, StrokeCap.Round)
            }
        }

        // Brand text below the icon
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
                .alpha(textAlpha.value)
                .offset(y = textOffset.value.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = "Pro",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Light,
                    color = SplashGray
                )
                Text(
                    text = "Scan",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = SplashBlue
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Scanează inteligent",
                fontSize = 13.sp,
                color = SplashGray.copy(alpha = tagAlpha.value),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
