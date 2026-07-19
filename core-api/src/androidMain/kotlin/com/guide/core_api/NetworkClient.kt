package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import ro.cosminmihu.ktor.monitor.ContentLength
import ro.cosminmihu.ktor.monitor.KtorMonitorLogging
import ro.cosminmihu.ktor.monitor.RetentionPeriod
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
    // Pasang KtorMonitor khusus untuk Android

    install(KtorMonitorLogging) {
        sanitizeHeader { header -> header == "Authorization" }
        filter { request -> !request.url.host.contains("cosminmihu.ro") }
        showNotification = true
        retentionPeriod = RetentionPeriod.OneHour
        maxContentLength = ContentLength.Default
    }
}