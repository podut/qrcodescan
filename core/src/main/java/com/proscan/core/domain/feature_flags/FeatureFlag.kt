package com.proscan.core.domain.feature_flags

object FeatureFlag {
    // Safety features
    const val PAYMENT_WARNING_DIALOG = "payment_warning_dialog"
    const val DOMAIN_HIGHLIGHT = "domain_highlight"
    const val DOMAIN_WHITELIST = "domain_whitelist"

    // Scanner
    const val BATCH_MODE = "batch_mode"

    // Generator
    const val BARCODE_FORMATS = "barcode_formats"
    const val SHARE_AS_IMAGE = "share_as_image"

    // History
    const val HISTORY_GROUPING = "history_grouping"
    const val HISTORY_NOTES = "history_notes"

    // UX
    const val ONBOARDING_SCREEN = "onboarding_screen"
    const val DARK_MODE = "dark_mode"

    // Monetization
    const val ADS_ENABLED = "ads_enabled"
    const val AD_FREQUENCY = "ad_frequency"  // Int value
    const val PRO_FEATURES = "pro_features"
}
