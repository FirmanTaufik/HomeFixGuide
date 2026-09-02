package com.home.fixguide

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.home.fixguide.helper.AppOpenAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GuideApp : Application() {

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}