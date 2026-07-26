package com.guide.core_api.repository

import com.guide.core_api.ApiService
import com.guide.core_api.Resource
import com.guide.core_api.model.BloggerResponse
import com.guide.core_api.toResource

interface BlogRepository {
    suspend fun getBlogResponse(url : String): Resource<BloggerResponse>
}

class BlogRepositoryImpl (
    private val apiService: ApiService
): BlogRepository{
    override suspend fun getBlogResponse(url : String): Resource<BloggerResponse> {
        return try {
            val response = apiService.getResponse(url)
            response.toResource()
        } catch (e: Exception) {
            print("UserRepositoryImplTAG " + e.cause?.message)
            Resource.Error("Gagal mengambil data pengguna, periksa koneksi Anda.", e)
        }
    }

}