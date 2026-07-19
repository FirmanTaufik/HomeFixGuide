package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE

actual fun createPlatformHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        engine {
            // Di sini Anda bisa menambahkan konfigurasi spesifik iOS/Darwin jika diperlukan
            configureSession {
                timeoutIntervalForRequest = 15.0
                timeoutIntervalForResource = 15.0
            }
        }
    }
}

actual fun HttpClientConfig<*>.installNetworkMonitor() {
    // Untuk iOS, kita pakai fitur Logger bawaan Ktor yang 100% stabil di iOS Native
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.BODY // Menampilkan seluruh request & response di Xcode Console
    }
}