package com.guide.core_api

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Exception? = null) : Resource<Nothing>()
    data object SessionExpired : Resource<Nothing>()
    data object Idle: Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}