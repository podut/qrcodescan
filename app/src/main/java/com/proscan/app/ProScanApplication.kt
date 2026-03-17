package com.proscan.app

import android.app.Application
import com.proscan.core.domain.feature_flags.FeatureFlagRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ProScanApplication : Application() {

    @Inject
    lateinit var featureFlagRepository: FeatureFlagRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            featureFlagRepository.fetchAndUpdateCache()
        }
    }
}
