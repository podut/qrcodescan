package com.proscan.app.navigation

sealed class Route(val route: String) {
    object Onboarding : Route("onboarding")
    object Scanner : Route("scanner")
    object History : Route("history")
    object Generator : Route("generator")
    object Settings : Route("settings")
    object Result : Route("result/{scanId}") {
        fun createRoute(scanId: String) = "result/$scanId"
    }
}
