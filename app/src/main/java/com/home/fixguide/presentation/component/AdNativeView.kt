package com.home.fixguide.presentation.component

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.home.fixguide.R

private const val TEST_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110"

@Composable
fun AdNativeView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val initialAdUnitId = adUnitId.ifBlank { TEST_NATIVE_AD_ID }
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(initialAdUnitId) {
        isLoading = true

        fun loadNativeAd(idToLoad: String, isRetry: Boolean = false) {
            Log.d("AdNativeView", "Loading Native Ad with ID: $idToLoad (isRetry=$isRetry)")

            val adLoader = AdLoader.Builder(context, idToLoad)
                .forNativeAd { ad ->
                    Log.d("AdNativeView", "Native Ad loaded successfully with ID: $idToLoad")
                    nativeAd?.destroy()
                    nativeAd = ad
                    isLoading = false
                }
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("AdNativeView", "Native Ad failed to load with ID $idToLoad (${loadAdError.code}): ${loadAdError.message}")
                        if (!isRetry && idToLoad != TEST_NATIVE_AD_ID) {
                            Log.d("AdNativeView", "Retrying Native Ad load with official test ID: $TEST_NATIVE_AD_ID")
                            loadNativeAd(TEST_NATIVE_AD_ID, isRetry = true)
                        } else {
                            isLoading = false
                        }
                    }
                })
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        loadNativeAd(initialAdUnitId)

        onDispose {
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    val currentAd = nativeAd
    if (currentAd != null) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            factory = { ctx ->
                val adView = LayoutInflater.from(ctx).inflate(R.layout.layout_native_ad, null) as NativeAdView
                populateNativeAdView(currentAd, adView)
                adView
            },
            update = { adView ->
                populateNativeAdView(currentAd, adView)
            }
        )
    } else if (isLoading) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
    val bodyView = adView.findViewById<TextView>(R.id.ad_body)
    val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)
    val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
    val mediaView = adView.findViewById<MediaView>(R.id.ad_media)

    adView.headlineView = headlineView
    adView.bodyView = bodyView
    adView.callToActionView = ctaView
    adView.iconView = iconView
    adView.mediaView = mediaView

    headlineView?.text = nativeAd.headline

    if (nativeAd.body.isNullOrBlank()) {
        bodyView?.visibility = View.GONE
    } else {
        bodyView?.visibility = View.VISIBLE
        bodyView?.text = nativeAd.body
    }

    if (nativeAd.callToAction.isNullOrBlank()) {
        ctaView?.visibility = View.GONE
    } else {
        ctaView?.visibility = View.VISIBLE
        ctaView?.text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
        iconView?.visibility = View.GONE
    } else {
        iconView?.visibility = View.VISIBLE
        iconView?.setImageDrawable(nativeAd.icon?.drawable)
    }

    if (mediaView != null && nativeAd.mediaContent != null) {
        mediaView.mediaContent = nativeAd.mediaContent
    }

    adView.setNativeAd(nativeAd)
}
