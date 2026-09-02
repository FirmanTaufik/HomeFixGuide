package com.home.fixguide

import android.app.Application
import com.home.fixguide.helper.AppOpenAdManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GuideApp : Application() {

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager
}