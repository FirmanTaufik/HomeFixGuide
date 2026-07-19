package com.guide.core.module


import com.guide.core_api.ApiService
import com.guide.core_api.ApiServiceImpl
import com.guide.core_api.GetUsersUseCase
import com.guide.core_api.UserRepository
import com.guide.core_api.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideApiService(): ApiService =
        ApiServiceImpl()

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService
    ): UserRepository =
        UserRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideGetUsersUseCase(
        repository: UserRepository
    ): GetUsersUseCase =
        GetUsersUseCase(repository)
}