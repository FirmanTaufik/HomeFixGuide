package com.home.fixguide.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fixguide_config_prefs")

@Singleton
class AppDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveConfig(configKey: ConfigKey, valueStr: String) {
        val prefKey = stringPreferencesKey(configKey.preferenceKey)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = valueStr
        }
    }

    suspend fun saveConfig(keyName: String, valueStr: String) {
        val configKey = ConfigKey.fromRawKey(keyName)
        val prefKey = if (configKey != null) {
            stringPreferencesKey(configKey.preferenceKey)
        } else {
            stringPreferencesKey(normalizeKey(keyName))
        }
        context.dataStore.edit { preferences ->
            preferences[prefKey] = valueStr
        }
    }

    suspend fun saveConfigs(configMap: Map<String, String>) {
        context.dataStore.edit { preferences ->
            configMap.forEach { (keyName, valueStr) ->
                val configKey = ConfigKey.fromRawKey(keyName)
                val prefKey = if (configKey != null) {
                    stringPreferencesKey(configKey.preferenceKey)
                } else {
                    stringPreferencesKey(normalizeKey(keyName))
                }
                preferences[prefKey] = valueStr
            }
        }
    }

    fun getConfig(configKey: ConfigKey): Flow<String?> {
        val prefKey = stringPreferencesKey(configKey.preferenceKey)
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun getConfig(keyName: String): Flow<String?> {
        val configKey = ConfigKey.fromRawKey(keyName)
        val prefKey = if (configKey != null) {
            stringPreferencesKey(configKey.preferenceKey)
        } else {
            stringPreferencesKey(normalizeKey(keyName))
        }
        return context.dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    fun getAllConfigs(): Flow<Map<String, String>> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().mapKeys { it.key.name }.mapValues { it.value.toString() }
        }
    }

    private fun normalizeKey(keyName: String): String {
        return keyName.trim().lowercase().replace(" ", "_")
    }
}
