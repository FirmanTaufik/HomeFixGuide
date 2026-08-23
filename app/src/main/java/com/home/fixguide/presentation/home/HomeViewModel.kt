package com.home.fixguide.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideSubCategory
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.data.local.ThemeManager
import com.home.fixguide.helper.executeTask
import com.home.fixguide.helper.mutableStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val guideCase: GuideCase,
    val themeManager: ThemeManager
) : BaseViewModel() {

    val isDarkMode = themeManager.isDarkMode

    fun toggleTheme(isSystemDark: Boolean) {
        themeManager.toggleDarkMode(isSystemDark)
    }

    var uiState by mutableStateDelegate<Resource<Pair<List<GuideCategory>, List<GuideSubCategory>>>>(
        Resource.Loading
    )

    var searchState by mutableStateDelegate<Resource<List<GuideCategory>>>(Resource.Idle)
    var searchQuery by mutableStateDelegate("")
    private var searchJob: Job? = null

    init {
        getCategory()
    }

    fun getCategory() = executeTask(
        Dispatchers.IO,
        onSuccess = {
            uiState.value = it as Resource<Pair<List<GuideCategory>, List<GuideSubCategory>>>
        }
    ) {
        guideCase.getGuides()
    }

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            searchState.value = Resource.Idle
            return
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500) // Debounce 500ms
            searchState.value = Resource.Loading
            try {
                val results = guideCase.searchGuides(newQuery)
                searchState.value = Resource.Success(results)
            } catch (e: Exception) {
                searchState.value = Resource.Error(e.message ?: "Failed to search guides", e)
            }
        }
    }

    fun clearSearch() {
        searchQuery.value = ""
        searchState.value = Resource.Idle
        searchJob?.cancel()
    }
}