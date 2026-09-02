package com.home.fixguide.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdManager @Inject constructor(
    private val application: Application,
    private val appDataStore: AppDataStore
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun fetchAd() {
        if (isAdAvailable() || isLoadingAd) return

        isLoadingAd = true
        CoroutineScope(Dispatchers.IO).launch {
            val adUnitId = appDataStore.getConfig(ConfigKey.ADMOB_OPEN_APP_ID).firstOrNull()
            if (adUnitId.isNullOrBlank()) {
                Log.d("AppOpenAdManager", "App Open Ad Unit ID is null or blank.")
                isLoadingAd = false
                return@launch
            }

            withContext(Dispatchers.Main) {
                val request = AdRequest.Builder().build()
                AppOpenAd.load(
                    application,
                    adUnitId,
                    request,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            Log.d("AppOpenAdManager", "App Open Ad Loaded successfully with ID: $adUnitId")
                            appOpenAd = ad
                            isLoadingAd = false
                            loadTime = Date().time
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.e("AppOpenAdManager", "App Open Ad failed to load: ${loadAdError.message}")
                            isLoadingAd = false
                        }
                    }
                )
            }
        }
    }

    fun showAdIfAvailable(activity: Activity) {
        if (!isShowingAd && isAdAvailable()) {
            Log.d("AppOpenAdManager", "Showing App Open Ad.")
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d("AppOpenAdManager", "App Open Ad dismissed.")
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.e("AppOpenAdManager", "App Open Ad failed to show: ${adError.message}")
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    Log.d("AppOpenAdManager", "App Open Ad showed.")
                }
            }
            appOpenAd?.show(activity)
        } else {
            Log.d("AppOpenAdManager", "App Open Ad not ready or already showing. Preloading ad.")
            fetchAd()
        }
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        currentActivity?.let { activity ->
            showAdIfAvailable(activity)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
