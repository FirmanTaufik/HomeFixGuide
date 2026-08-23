package com.home.fixguide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guide.core_api.usecase.GetUsersUseCase
import com.guide.core_api.Resource
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.model.BloggerResponse
import com.guide.core_api.usecase.GetBlogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    val getUsersUseCase: GetUsersUseCase,
    val getBlogUseCase: GetBlogUseCase,
    val guideCase: GuideCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<BloggerResponse>>(Resource.Idle)
    val uiState: StateFlow<Resource<BloggerResponse>> = _uiState.asStateFlow()


    fun getUser(){
        viewModelScope.launch {
            _uiState.value = Resource.Loading
        }
    }
}
