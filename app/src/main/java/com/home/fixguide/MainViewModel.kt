package com.home.fixguide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.BloggerResponse
import com.guide.core_api.usecase.GetBlogUseCase
import com.guide.core_api.usecase.GetUsersUseCase
import com.home.fixguide.data.local.AppDataStore
import com.home.fixguide.data.local.ConfigKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val getUsersUseCase: GetUsersUseCase,
    val getBlogUseCase: GetBlogUseCase,
    val guideCase: GuideCase,
    val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<BloggerResponse>>(Resource.Idle)
    val uiState: StateFlow<Resource<BloggerResponse>> = _uiState.asStateFlow()

    val bannerAdId: StateFlow<String?> = appDataStore.getConfig(ConfigKey.ADMOB_BANNER_ID)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun getUser() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading
        }
    }
}
