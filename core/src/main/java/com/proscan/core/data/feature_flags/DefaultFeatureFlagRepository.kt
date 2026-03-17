package com.proscan.core.data.feature_flags

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.proscan.core.domain.feature_flags.FeatureFlagConfig
import com.proscan.core.domain.feature_flags.FeatureFlagRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

private val Context.flagsDataStore: DataStore<Preferences> by preferencesDataStore(name = "feature_flags")

private const val REMOTE_CONFIG_URL =
    "https://raw.githubusercontent.com/podut/qrcodescan/main/remote_config.json"
private const val FETCH_INTERVAL_MS = 6 * 60 * 60 * 1000L // 6 hours

@Singleton
class DefaultFeatureFlagRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : FeatureFlagRepository {

    private val KEY_CACHED_JSON = stringPreferencesKey("remote_config_json")
    private val KEY_LAST_FETCH = longPreferencesKey("remote_config_last_fetch")

    private val _config = AtomicReference(FeatureFlagConfig.DEFAULT)

    init {
        // Load from cache synchronously on first access
        // (DataStore first() will be called lazily when fetchAndUpdateCache runs)
    }

    override fun getConfig(): FeatureFlagConfig = _config.get()

    override suspend fun fetchAndUpdateCache() {
        val prefs = context.flagsDataStore.data.first()
        val lastFetch = prefs[KEY_LAST_FETCH] ?: 0L
        val cachedJson = prefs[KEY_CACHED_JSON]

        // Populate in-memory config from cache first (fast path)
        if (!cachedJson.isNullOrBlank()) {
            _config.set(parseJson(cachedJson))
        }

        // Only hit network if cache is stale
        if (System.currentTimeMillis() - lastFetch < FETCH_INTERVAL_MS && cachedJson != null) return

        try {
            val json = fetchJson(REMOTE_CONFIG_URL)
            if (json != null) {
                _config.set(parseJson(json))
                context.flagsDataStore.edit { it[KEY_CACHED_JSON] = json; it[KEY_LAST_FETCH] = System.currentTimeMillis() }
            }
        } catch (_: Exception) {
            // Keep existing config, no crash
        }
    }

    private fun fetchJson(urlStr: String): String? {
        val conn = URL(urlStr).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) conn.inputStream.bufferedReader().readText() else null
        } catch (_: Exception) { null } finally { conn.disconnect() }
    }

    private fun parseJson(json: String): FeatureFlagConfig {
        return try {
            val root = JSONObject(json)
            val flags = root.optJSONObject("flags") ?: return FeatureFlagConfig.DEFAULT
            FeatureFlagConfig(
                paymentWarningDialog = flags.optBoolean("payment_warning_dialog", true),
                domainHighlight      = flags.optBoolean("domain_highlight", true),
                domainWhitelist      = flags.optBoolean("domain_whitelist", false),
                batchMode            = flags.optBoolean("batch_mode", true),
                barcodeFormats       = flags.optBoolean("barcode_formats", true),
                shareAsImage         = flags.optBoolean("share_as_image", true),
                historyGrouping      = flags.optBoolean("history_grouping", false),
                historyNotes         = flags.optBoolean("history_notes", false),
                onboardingScreen     = flags.optBoolean("onboarding_screen", false),
                darkMode             = flags.optBoolean("dark_mode", false),
                adsEnabled           = flags.optBoolean("ads_enabled", false),
                adFrequency          = flags.optInt("ad_frequency", 5),
                proFeatures          = flags.optBoolean("pro_features", true)
            )
        } catch (_: Exception) { FeatureFlagConfig.DEFAULT }
    }
}
