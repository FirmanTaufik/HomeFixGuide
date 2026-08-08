package com.home.fixguide.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideSubCategory
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.helper.executeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val guideCase: GuideCase,
) : BaseViewModel() {

    var uiState by mutableStateDelegate<Resource<Pair<List<GuideCategory>, List<GuideSubCategory>>>>(
        Resource.Loading
    )


    init {
        getCategory()
    }

    fun getCategory() {
        executeTask(
            Dispatchers.IO,
            onLoading = {

            },
            onError = {

                Log.d("FirmanTAG", "executeTask: ${it}")
            },
            onComplete = {

            }) {
            /*uiState.value = */guideCase.getGuides()
        }
    }


}