package com.home.fixguide.presentation.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.guide.GuideDetailCategory
import com.guide.core_api.usecase.GetBlogUseCase
import com.guide.core_api.usecase.GetUsersUseCase
import com.home.fixguide.base.BaseViewModel
import com.home.fixguide.helper.ExceptionParser
import com.home.fixguide.helper.executeTask
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class CategoryViewModel @Inject constructor(
    val guideCase: GuideCase
) : BaseViewModel() {

    var uiState by mutableStateDelegate<Resource<GuideDetailCategory>>(Resource.Loading)

    fun getDetail(url : String){
      /*  executeTask(dispatcher = Dispatchers.IO){
            guideCase.getDetailCategory(url)
        }*/
    }

    override fun generateDisplayError(exception: Exception, onError: (String) -> Unit) {
        Log.d("FirmanTAG", "generateDisplayError: ")
        super.generateDisplayError(exception, onError)
    }

}