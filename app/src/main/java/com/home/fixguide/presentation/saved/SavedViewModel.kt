package com.home.fixguide.presentation.saved

import androidx.lifecycle.viewModelScope
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import com.home.fixguide.data.local.SavedGuideEntity
import com.home.fixguide.data.repository.SavedGuideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedGuideRepository,
    private val appDataStore: AppDataStore
) : BaseViewModel() {

    val savedGuides: StateFlow<List<SavedGuideEntity>> = repository.getAllSavedGuides()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun removeSavedGuide(url: String) {
        viewModelScope.launch {
            repository.removeGuide(url)
        }
    }
}
