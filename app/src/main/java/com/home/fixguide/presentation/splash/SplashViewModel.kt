package com.home.fixguide.presentation.splash

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.guide.core_api.guidecase.GuideCase
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import com.home.fixguide.helper.AppOpenAdManager
import com.home.fixguide.helper.InterstitialAdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val guideCase: GuideCase,
    private val appDataStore: AppDataStore,
    private val appOpenAdManager: AppOpenAdManager,
    private val interstitialAdManager: InterstitialAdManager
) : BaseViewModel() {

    init {
        fetchUpdateData()
    }

    fun fetchUpdateData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updates = guideCase.getRepairClinicUpdates()
                if (updates.isEmpty()) {
                    Log.d("SplashViewModel", "ol > li updates list is empty")
                } else {
                    updates.forEachIndexed { index, text ->
                        Log.d("SplashViewModel", "Raw ol > li [$index]: $text")
                        if (text.contains(":")) {
                            val parts = text.split(":", limit = 2)
                            val rawKey = parts[0].trim()
                            val valueAfterColon = parts[1].trim()

                            val configKey = ConfigKey.fromRawKey(rawKey)
                            if (configKey != null) {
                                appDataStore.saveConfig(configKey, valueAfterColon)
                                Log.d(
                                    "SplashViewModel",
                                    "Saved to DataStore [Enum: ${configKey.name}] -> Key: '${configKey.preferenceKey}' | Value: '$valueAfterColon'"
                                )
                            } else {
                                appDataStore.saveConfig(rawKey, valueAfterColon)
                                Log.d(
                                    "SplashViewModel",
                                    "Saved to DataStore [RawString] -> Key: '$rawKey' | Value: '$valueAfterColon'"
                                )
                            }
                        } else {
                            Log.w("SplashViewModel", "Item without colon [$index]: $text")
                        }
                    }
                    // Preload App Open Ad & Interstitial Ad once config keys are stored
                    appOpenAdManager.fetchAd()
                    interstitialAdManager.preloadAd()
                }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Error fetching update data: ${e.message}", e)
            }
        }
    }
}
