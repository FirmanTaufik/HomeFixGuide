package com.home.fixguide.helper

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterstitialAdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appDataStore: AppDataStore
) {

    private var interstitialAd: InterstitialAd? = null
    private var isLoadingAd = false
    private var showAds = true
    private var countDownTimer: CountDownTimer? = null

    fun startCooldownTimer() {
        showAds = false
        countDownTimer?.cancel()

        CoroutineScope(Dispatchers.IO).launch {
            val rawInterval = appDataStore.getConfig(ConfigKey.INTERVAL_INTER).firstOrNull()
            val seconds = rawInterval?.toLongOrNull() ?: 60L
            val millisInFuture = seconds * 1000L
            Log.d("InterstitialAdManager", "Memulai timer cooldown inter: $seconds detik ($millisInFuture ms)")

            withContext(Dispatchers.Main) {
                countDownTimer = object : CountDownTimer(millisInFuture, 1000L) {
                    override fun onTick(millisUntilFinished: Long) {
                        val sisaDetik = millisUntilFinished / 1000
                        Log.d("InterstitialAdManager", "onTick cooldown: $sisaDetik detik lagi")
                    }

                    override fun onFinish() {
                        Log.d("InterstitialAdManager", "Cooldown selesai. Interstitial Ad dapat ditampilkan kembali.")
                        showAds = true
                    }
                }.start()
            }
        }
    }

    fun preloadAd() {
        if (interstitialAd != null || isLoadingAd) return
        isLoadingAd = true

        CoroutineScope(Dispatchers.IO).launch {
            val adUnitId = appDataStore.getConfig(ConfigKey.ADMOB_INTERSTITIAL_ID).firstOrNull()
            if (adUnitId.isNullOrBlank()) {
                Log.d("InterstitialAdManager", "Interstitial Ad Unit ID is null or blank.")
                isLoadingAd = false
                return@launch
            }

            withContext(Dispatchers.Main) {
                val request = AdRequest.Builder().build()
                InterstitialAd.load(
                    context,
                    adUnitId,
                    request,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d("InterstitialAdManager", "Interstitial Ad Loaded successfully with ID: $adUnitId")
                            interstitialAd = ad
                            isLoadingAd = false
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.e("InterstitialAdManager", "Interstitial Ad failed to load: ${loadAdError.message}")
                            interstitialAd = null
                            isLoadingAd = false
                        }
                    }
                )
            }
        }
    }

    fun tryOpenInterAds(activity: Activity, onAdDismissed: () -> Unit) {
        if (showAds) {
            showInterstitial(activity, onAdDismissed)
        } else {
            Log.d("InterstitialAdManager", "Iklan Interstitial sedang cooldown (showAds = false). Melanjutkan navigasi.")
            onAdDismissed()
        }
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("InterstitialAdManager", "Interstitial Ad dismissed.")
                    interstitialAd = null
                    startCooldownTimer()
                    preloadAd()
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("InterstitialAdManager", "Interstitial Ad failed to show: ${adError.message}")
                    interstitialAd = null
                    startCooldownTimer()
                    preloadAd()
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("InterstitialAdManager", "Interstitial Ad showed.")
                }
            }
            ad.show(activity)
        } else {
            Log.d("InterstitialAdManager", "Interstitial Ad belum siap. Preload dan lanjut navigasi.")
            preloadAd()
            onAdDismissed()
        }
    }
}
