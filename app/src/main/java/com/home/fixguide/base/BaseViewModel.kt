package com.home.fixguide.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.guide.core.manager.NetworkManager
import com.home.fixguide.helper.ExceptionParser
import com.home.fixguide.helper.mutableStateDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel(), ExceptionParser{

    var showCommontError  by mutableStateDelegate(false)

    @Inject
    lateinit var networkManager : NetworkManager

    override fun generateDisplayError(exception: Exception, onError: (String) -> Unit) {
        showCommontError.value = true
        Log.d("FirmanTAG", "generateDisplayError: ${exception.message}")
        super.generateDisplayError(exception, onError)
    }

    suspend fun initNetworkStatus(){
        networkManager.isNetworkAvailable.collect { it ->
            showCommontError.value = !it
        }
    }
}