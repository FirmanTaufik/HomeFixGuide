package com.guide.core_api

class AppContainer {

    private val httpClient = Client.createHttpClient()

    val apiService = ApiServiceImpl(httpClient)
    val userRepository = UserRepositoryImpl(apiService)
    val getUsersUseCase = GetUsersUseCase(userRepository)
}