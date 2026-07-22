package com.guide.core_api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.OkHttpClient

import java.util.concurrent.TimeUnit

actual object HttpClientFactory {

     var interceptorChucker: Interceptor ?= null

     actual fun create(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                config {
                    connectTimeout(15, TimeUnit.SECONDS)
                    readTimeout(15, TimeUnit.SECONDS)
                    interceptorChucker?.let {
                        addInterceptor (it)
                    }
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

//actual fun createPlatformHttpClient(
//
//): HttpClient {
//    return HttpClient(OkHttp) {
//        engine {
//            // Di sini Anda bisa menambahkan konfigurasi OkHttp seperti Interceptor, Timeout, dll.
//            config {
//                connectTimeout(15, TimeUnit.SECONDS)
//                readTimeout(15, TimeUnit.SECONDS)
//            }
//            interceptor?.let {
//                addInterceptor(it)
//            }
//        }
//    }
//}


    // Pasang KtorMonitor khusus untuk Android

 /*   install(KtorMonitorLogging) {
        sanitizeHeader { header -> header == "Authorization" }
        filter { request -> !request.url.host.contains("cosminmihu.ro") }
        showNotification = true
        retentionPeriod = RetentionPeriod.OneHour
        maxContentLength = ContentLength.Default
    }*/
