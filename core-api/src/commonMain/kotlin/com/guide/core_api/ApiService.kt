package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
class ApiService() {
    val client = Client.createHttpClient()
    suspend fun getUsers(): List<User> {
        return try {
            client.get("https://jsonplaceholder.typicode.com/users") {
                contentType(ContentType.Application.Json)
            }.body<List<User>>()
        } catch (e: Exception) {
            // Handle error atau lempar custom exception di sini
            emptyList()
        }
    }
}