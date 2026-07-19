package com.guide.core_api

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// 1. Definisikan expect function untuk mendapatkan engine spesifik platform
expect fun createPlatformHttpClient(): HttpClient
expect fun HttpClientConfig<*>.installNetworkMonitor()

object Client  {
    // 2. Buat fungsi helper untuk konfigurasi dasar client
    fun createHttpClient(): HttpClient {
        return createPlatformHttpClient().config {
            // Plugin untuk otomatis convert JSON ke Data Class (dan sebaliknya)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // Sangat disarankan agar app tidak crash jika API menambah field baru
                })
            }

            //    installNetworkMonitor()

            // Plugin untuk Logging (sangat berguna saat debugging)
            /*install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }*/

        }
    }
}