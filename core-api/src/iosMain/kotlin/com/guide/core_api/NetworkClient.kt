package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual object HttpClientFactory {
    actual fun create(): HttpClient {
         return HttpClient(Darwin) {
             engine {
                 // Di sini Anda bisa menambahkan konfigurasi spesifik iOS/Darwin jika diperlukan
                 configureSession {
                     timeoutIntervalForRequest = 15.0
                     timeoutIntervalForResource = 15.0
                 }
             }

             install(ContentNegotiation) {
                 json(Json {
                     prettyPrint = true
                     isLenient = true
                     ignoreUnknownKeys = true // Sangat disarankan agar app tidak crash jika API menambah field baru
                 })
             }
         }
    }
}

/*actual fun createPlatformHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        engine {
            // Di sini Anda bisa menambahkan konfigurasi spesifik iOS/Darwin jika diperlukan
            configureSession {
                timeoutIntervalForRequest = 15.0
                timeoutIntervalForResource = 15.0
            }
        }
    }
}*/
