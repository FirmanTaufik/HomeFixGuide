package com.home.fixguide.presentation.category

import android.app.Activity
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.guide.GuideDetailCategory
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import com.home.fixguide.data.repository.SavedGuideRepository
import com.home.fixguide.helper.InterstitialAdManager
import com.home.fixguide.helper.executeTask
import com.home.fixguide.helper.mutableStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    val guideCase: GuideCase,
    private val savedRepository: SavedGuideRepository,
    val interstitialAdManager: InterstitialAdManager,
    private val appDataStore: AppDataStore
) : BaseViewModel() {

    var uiState by mutableStateDelegate<Resource<GuideDetailCategory>>(Resource.Loading)

    val nativeAdId: StateFlow<String?> = appDataStore.getConfig(ConfigKey.ADMOB_NATIVE_ID)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val nativeAdInterval: StateFlow<Int> = appDataStore.getConfig(ConfigKey.INTERVAL_NATIVE)
        .map { it?.toIntOrNull() ?: 2 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2
        )

    init {
        interstitialAdManager.preloadAd()
    }

    fun showInterstitialAd(activity: Activity?, onAdDismissed: () -> Unit) {
        if (activity != null) {
            interstitialAdManager.tryOpenInterAds(activity, onAdDismissed)
        } else {
            onAdDismissed()
        }
    }

    fun isSaved(url: String): Flow<Boolean> {
        return savedRepository.isSaved(url)
    }

    fun toggleSave(url: String, title: String, imageUrl: String, isCurrentSaved: Boolean) {
        viewModelScope.launch {
            savedRepository.toggleSave(
                url = url,
                title = title,
                imageUrl = imageUrl,
                categoryType = if (url.contains("/Guide/")) "guide" else "device",
                currentSavedStatus = isCurrentSaved
            )
        }
    }

    fun getDetail(url: String) = executeTask(
        dispatcher = Dispatchers.IO,
        onLoading = {
            uiState.value = Resource.Loading
        },
        onSuccess = {
            uiState.value = it as Resource<GuideDetailCategory>
        }
    ) {
        guideCase.getDetailCategory(url)
    }

    override fun generateDisplayError(exception: Exception, onError: (String) -> Unit) {
        Log.d("CategoryViewModel", "generateDisplayError: ${exception.message}")
        uiState.value = Resource.Error(exception.message ?: "Failed to load data", exception)
        super.generateDisplayError(exception, onError)
    }
}
