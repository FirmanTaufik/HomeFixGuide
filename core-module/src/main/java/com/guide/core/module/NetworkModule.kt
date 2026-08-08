package com.guide.core.module


import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.guide.core_api.ApiService
import com.guide.core_api.ApiServiceImpl
import com.guide.core_api.usecase.GetUsersUseCase
import com.guide.core_api.HttpClientFactory
import com.guide.core_api.guidecase.GuideCase
import com.guide.core_api.repository.BlogRepository
import com.guide.core_api.repository.BlogRepositoryImpl
import com.guide.core_api.repository.UserRepository
import com.guide.core_api.repository.UserRepositoryImpl
import com.guide.core_api.usecase.GetBlogUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context
    ): HttpClient {
        HttpClientFactory.interceptorChucker = ChuckerInterceptor(context)
        return HttpClientFactory.create()
    }

    @Provides
    @Singleton
    fun provideApiService(
        httpClient: HttpClient
    ): ApiService =
        ApiServiceImpl(httpClient)


    @Provides
    @Singleton
    fun provideBlogRepository(
        apiService: ApiService
    ): BlogRepository =
        BlogRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideGetGetBlogUseCase(
        repository: BlogRepository
    ): GetBlogUseCase =
        GetBlogUseCase(repository)

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

    @Provides
    @Singleton
    fun provideGuideUseCase() = GuideCase()

}