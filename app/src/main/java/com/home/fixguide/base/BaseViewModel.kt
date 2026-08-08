package com.home.fixguide.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.home.fixguide.helper.ExceptionParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel(), ExceptionParser{

    fun <T> mutableStateDelegate(
        defaultValue: T
    ): ReadWriteProperty<Any?, MutableStateFlow<T>> =
        object : ReadWriteProperty<Any?, MutableStateFlow<T>> {

            private val flow = MutableStateFlow(defaultValue)

            override fun getValue(
                thisRef: Any?,
                property: KProperty<*>
            ): MutableStateFlow<T> = flow

            override fun setValue(
                thisRef: Any?,
                property: KProperty<*>,
                value: MutableStateFlow<T>
            ) {
                flow.value = value.value
            }
        }

    override fun generateDisplayError(exception: Exception, onError: (String) -> Unit) {
        Log.d("FirmanTAG", "generateDisplayError: ${exception.message}")
        super.generateDisplayError(exception, onError)
    }
}