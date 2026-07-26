package com.guide.core_api.repository

import com.guide.core_api.ApiService
import com.guide.core_api.Resource
import com.guide.core_api.model.User
import com.guide.core_api.toResource

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
            print("UserRepositoryImplTAG " + e.cause?.message)
            Resource.Error("Gagal mengambil data pengguna, periksa koneksi Anda.", e)
        }
    }
}