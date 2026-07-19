package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE

import java.util.concurrent.TimeUnit

actual fun createPlatformHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            // Di sini Anda bisa menambahkan konfigurasi OkHttp seperti Interceptor, Timeout, dll.
            config {
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
            }
        }
    }
}

actual fun HttpClientConfig<*>.installNetworkMonitor() {



    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.BODY // Menampilkan seluruh request & response di Xcode Console
    }
    // Pasang KtorMonitor khusus untuk Android

 /*   install(KtorMonitorLogging) {
        sanitizeHeader { header -> header == "Authorization" }
        filter { request -> !request.url.host.contains("cosminmihu.ro") }
        showNotification = true
        retentionPeriod = RetentionPeriod.OneHour
        maxContentLength = ContentLength.Default
    }*/
}