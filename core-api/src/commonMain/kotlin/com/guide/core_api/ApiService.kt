package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*


interface ApiService {
    suspend fun getUsers(): HttpResponse
    suspend fun getResponse(url: String): HttpResponse
}


class ApiServiceImpl(private val client: HttpClient = Client.createHttpClient()) : ApiService {
    override suspend fun getUsers(): HttpResponse {
        return client.get("https://jsonplaceholder.typicode.com/users") {
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun getResponse(url: String): HttpResponse {
        return client.get(url) {
            contentType(ContentType.Application.Json)
        }
    }
}