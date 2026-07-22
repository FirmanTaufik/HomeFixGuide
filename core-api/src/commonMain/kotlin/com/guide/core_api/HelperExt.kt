package com.guide.core_api

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.toResource(): Resource<T> {
    return if (status.isSuccess()) {
        Resource.Success(body<T>())
    } else {
        when (status.value) {
            401 -> Resource.SessionExpired
            else -> {
                print("toResourceTAG  ${status.value}")
                Resource.Error("Gagal mengambil data. Status code: ${status.value}")
            }
        }
    }
}