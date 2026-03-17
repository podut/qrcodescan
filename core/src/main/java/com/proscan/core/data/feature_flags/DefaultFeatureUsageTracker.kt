package com.proscan.core.data.feature_flags

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.proscan.core.domain.feature_flags.FeatureUsageTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.usageDataStore: DataStore<Preferences> by preferencesDataStore(name = "feature_usage")

@Singleton
class DefaultFeatureUsageTracker @Inject constructor(
    @ApplicationContext private val context: Context
) : FeatureUsageTracker {

    override suspend fun track(featureKey: String) {
        val key = intPreferencesKey("usage_$featureKey")
        context.usageDataStore.edit { prefs ->
            prefs[key] = (prefs[key] ?: 0) + 1
        }
    }

    override suspend fun getCount(featureKey: String): Int {
        val key = intPreferencesKey("usage_$featureKey")
        return context.usageDataStore.data.first()[key] ?: 0
    }

    override suspend fun getAllCounts(): Map<String, Int> {
        val prefs = context.usageDataStore.data.first()
        return prefs.asMap()
            .filter { it.key.name.startsWith("usage_") }
            .mapKeys { it.key.name.removePrefix("usage_") }
            .mapValues { it.value as? Int ?: 0 }
    }
}
