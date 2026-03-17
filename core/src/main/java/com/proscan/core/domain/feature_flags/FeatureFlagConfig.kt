package com.proscan.core.domain.feature_flags

data class FeatureFlagConfig(
    // Safety
    val paymentWarningDialog: Boolean = true,
    val domainHighlight: Boolean = true,
    val domainWhitelist: Boolean = false,
    // Scanner
    val batchMode: Boolean = true,
    // Generator
    val barcodeFormats: Boolean = true,
    val shareAsImage: Boolean = true,
    // History
    val historyGrouping: Boolean = false,
    val historyNotes: Boolean = false,
    // UX
    val onboardingScreen: Boolean = false,
    val darkMode: Boolean = false,
    // Monetization
    val adsEnabled: Boolean = false,
    val adFrequency: Int = 5,
    val proFeatures: Boolean = true
) {
    companion object {
        val DEFAULT = FeatureFlagConfig()
    }
}
