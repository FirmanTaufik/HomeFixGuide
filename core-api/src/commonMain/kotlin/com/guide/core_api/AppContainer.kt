package com.guide.core_api

import com.guide.core_api.repository.UserRepositoryImpl
import com.guide.core_api.usecase.GetUsersUseCase

class AppContainer {

    private val httpClient = Client.createHttpClient()

    val apiService = ApiServiceImpl(httpClient)
    val userRepository = UserRepositoryImpl(apiService)
    val getUsersUseCase = GetUsersUseCase(userRepository)
}