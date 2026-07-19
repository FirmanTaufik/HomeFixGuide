package com.guide.core_api

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

interface UserRepository {
    suspend fun getUsers(): Resource<List<User>>
}

class UserRepositoryImpl(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getUsers(): Resource<List<User>> {
        return try {
            val response = apiService.getUsers()
            response.toResource()
        } catch (e: Exception) {
            Resource.Error("Gagal mengambil data pengguna, periksa koneksi Anda.", e)
        }
    }
}