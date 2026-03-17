package com.proscan.core.domain.feature_flags

interface FeatureUsageTracker {
    /** Increment usage count for this feature flag key */
    suspend fun track(featureKey: String)
    /** Get how many times a feature has been used */
    suspend fun getCount(featureKey: String): Int
    /** Get all usage counts as a map */
    suspend fun getAllCounts(): Map<String, Int>
}
