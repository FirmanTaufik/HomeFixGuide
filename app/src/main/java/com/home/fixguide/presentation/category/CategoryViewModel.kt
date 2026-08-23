package com.home.fixguide.presentation.category

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.guide.GuideDetailCategory
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.data.repository.SavedGuideRepository
import com.home.fixguide.helper.executeTask
import com.home.fixguide.helper.mutableStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    val guideCase: GuideCase,
    private val savedRepository: SavedGuideRepository
) : BaseViewModel() {

    var uiState by mutableStateDelegate<Resource<GuideDetailCategory>>(Resource.Loading)

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

