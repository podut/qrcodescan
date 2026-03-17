package com.proscan.core.domain.feature_flags

interface FeatureFlagRepository {
    /** Returns the currently cached config (synchronous, never suspends). Falls back to DEFAULT. */
    fun getConfig(): FeatureFlagConfig

    /** Fetches remote config and updates cache. Call on app start in background. */
    suspend fun fetchAndUpdateCache()
}
