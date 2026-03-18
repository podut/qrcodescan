package com.proscan.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proscan.app.navigation.ProScanNavHost
import com.proscan.app.navigation.Route
import com.proscan.core_ui.components.ProScanTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProScanApp(
    initialSharedText: String? = null,
    hasSeenOnboarding: Boolean = true,
    onOnboardingDone: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isScannerScreen = currentRoute == Route.Scanner.route
    val isResultScreen = currentRoute?.startsWith("result/") == true
    val isOnboardingScreen = currentRoute == Route.Onboarding.route

    val showTopBar = !isScannerScreen && !isResultScreen && !isOnboardingScreen
    val showBottomBar = !isResultScreen && !isOnboardingScreen

    Scaffold(
        topBar = {
            if (showTopBar) {
                ProScanTopBar(
                    onSettingsClick = {
                        navController.navigate(Route.Settings.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                ProScanBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    isScannerActive = isScannerScreen
                )
            }
        }
    ) { paddingValues ->
        ProScanNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            initialSharedText = initialSharedText,
            hasSeenOnboarding = hasSeenOnboarding,
            onOnboardingDone = onOnboardingDone
        )
    }
}

@Composable
private fun ProScanBottomBar(
    navController: NavHostController,
    currentRoute: String?,
    isScannerActive: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(80.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // History button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Route.History.route) {
                            popUpTo(Route.Scanner.route)
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == Route.History.route) Icons.Filled.History else Icons.Outlined.History,
                        contentDescription = "Istoric",
                        tint = if (currentRoute == Route.History.route) primaryColor else inactiveColor
                    )
                }
                Text(
                    text = "Istoric",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentRoute == Route.History.route) primaryColor else inactiveColor
                )
            }

            // Center FAB
            ScannerFab(
                isActive = isScannerActive,
                onClick = {
                    navController.navigate(Route.Scanner.route) {
                        popUpTo(Route.Scanner.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            // Generator button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Route.Generator.route) {
                            popUpTo(Route.Scanner.route)
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == Route.Generator.route) Icons.Filled.History else Icons.Outlined.QrCode,
                        contentDescription = "Generează",
                        tint = if (currentRoute == Route.Generator.route) primaryColor else inactiveColor
                    )
                }
                Text(
                    text = "Generează",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (currentRoute == Route.Generator.route) primaryColor else inactiveColor
                )
            }
        }
    }
}

@Composable
private fun ScannerFab(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val gradient = Brush.horizontalGradient(listOf(primaryColor, tertiaryColor))

    val rippleScales = remember { List(3) { Animatable(0f) } }
    val rippleAlphas = remember { List(3) { Animatable(0f) } }

    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun launchRipple() {
        scope.launch {
            rippleScales.forEachIndexed { index, scale ->
                launch {
                    delay(index * 110L)
                    scale.snapTo(0f)
                    rippleAlphas[index].snapTo(0.55f)
                    launch {
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(650, easing = FastOutLinearInEasing)
                        )
                    }
                    rippleAlphas[index].animateTo(
                        targetValue = 0f,
                        animationSpec = tween(650)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = 32.dp.toPx()
            val maxExtra = 30.dp.toPx()

            rippleScales.forEachIndexed { i, scale ->
                drawCircle(
                    color = primaryColor.copy(alpha = rippleAlphas[i].value),
                    radius = baseRadius + scale.value * maxExtra,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(pulseScale)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(gradient)
                .clickable {
                    launchRipple()
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scanează",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
