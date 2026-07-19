package com.home.fixguide

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guide.core_api.GetUsersUseCase
import com.guide.core_api.Resource
import com.guide.core_api.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<User>>>(Resource.Idle)
    val uiState: StateFlow<Resource<List<User>>> = _uiState.asStateFlow()


    fun getUser(){
        viewModelScope.launch {
            // 1. Pastikan state menjadi Loading sebelum memanggil API
            _uiState.value = Resource.Loading

            // 2. Eksekusi Use Case dan langsung assign hasilnya ke _uiState
            _uiState.value = getUsersUseCase()
        }
    }
}