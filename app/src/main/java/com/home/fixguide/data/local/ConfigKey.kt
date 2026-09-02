package com.home.fixguide.data.local

enum class ConfigKey(val rawKeyName: String, val preferenceKey: String) {
    ADMOB_APP_ID("Admob Id App", "admob_app_id"),
    ADMOB_BANNER_ID("Banner Admob", "admob_banner_id"),
    ADMOB_INTERSTITIAL_ID("Admob Interstitial", "admob_interstitial_id"),
    ADMOB_NATIVE_ID("Admob Native", "admob_native_id"),
    ADMOB_OPEN_APP_ID("AdMob Open App", "admob_open_app_id"),
    INTERVAL_INTER("Interval Inter", "interval_inter"),
    INTERVAL_NATIVE("Interval Native", "interval_native");

    companion object {
        fun fromRawKey(rawKey: String): ConfigKey? {
            val cleanRaw = rawKey.trim().lowercase()
            return entries.find { key ->
                key.rawKeyName.equals(cleanRaw, ignoreCase = true) ||
                        key.preferenceKey.equals(cleanRaw.replace(" ", "_"), ignoreCase = true) ||
                        cleanRaw.contains(key.rawKeyName.lowercase())
            }
        }
    }
}
