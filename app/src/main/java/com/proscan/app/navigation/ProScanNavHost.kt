package com.proscan.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.proscan.app.OnboardingScreen
import com.proscan.generator_presentation.GeneratorScreen
import com.proscan.history_presentation.HistoryScreen
import com.proscan.result_presentation.ResultScreen
import com.proscan.scanner_presentation.ScannerScreen
import com.proscan.settings_presentation.SettingsScreen

@Composable
fun ProScanNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialSharedText: String? = null,
    hasSeenOnboarding: Boolean = true,
    onOnboardingDone: () -> Unit = {}
) {
    LaunchedEffect(initialSharedText) {
        if (!initialSharedText.isNullOrBlank()) {
            navController.navigate(Route.Generator.route) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (hasSeenOnboarding) Route.Scanner.route else Route.Onboarding.route,
        modifier = modifier
    ) {
        composable(Route.Onboarding.route) {
            OnboardingScreen(onDone = {
                onOnboardingDone()
                navController.navigate(Route.Scanner.route) {
                    popUpTo(Route.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Route.Scanner.route) {
            ScannerScreen(
                onNavigate = { route -> navController.navigate(route) },
                onNavigateUp = {
                    // Scanner is the start destination — pop back stack if possible,
                    // otherwise navigate to History as the fallback "home"
                    if (!navController.popBackStack()) {
                        navController.navigate(Route.History.route) {
                            popUpTo(Route.Scanner.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Route.History.route) {
            HistoryScreen(
                onScanClick = { scanId ->
                    navController.navigate(Route.Result.createRoute(scanId))
                }
            )
        }

        composable(Route.Generator.route) {
            GeneratorScreen(sharedText = initialSharedText)
        }

        composable(Route.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Route.Result.route,
            arguments = listOf(navArgument("scanId") { type = NavType.StringType })
        ) {
            ResultScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
