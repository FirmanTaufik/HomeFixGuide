package com.home.fixguide.helper

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.network.HttpException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.IOException

fun String.toLocalDeviceFormat(): String {
    return try {
        val parsedDate = ZonedDateTime.parse(this)
        val localDate = parsedDate.withZoneSameInstant(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern(
            "dd MMMM yyyy, HH:mm z",
            Locale("id", "ID")
        )

        localDate.format(formatter)
    } catch (e: Exception) {
        // Fallback if the string is broken or null
        this
    }
}

fun Modifier.shimmerEffect(isLoading: Boolean = true): Modifier = composed {
    if (!isLoading) return@composed this

    // Menggunakan warna abu-abu (LightGray) yang pasti terlihat jelas di Light Mode maupun Dark Mode
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f, // Diperbesar agar gradien menyapu seluruh lebar layar dengan baik
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )

    // Langsung return background modifier
    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - 1000f, y = translateAnimation.value - 1000f),
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    )
}

fun Modifier.invisible(isInvisible: Boolean = true): Modifier {
    return this.alpha(if (isInvisible) 0f else 1f)
}

fun Modifier.gone(isGone: Boolean = true): Modifier {
    return if (isGone) {
        // Mencegah Compose untuk mengukur dan menggambar layout ini
        this.layout { _, _ -> layout(0, 0) {} }
    } else {
        this
    }
}

inline fun <T> T.executeTask(
    dispatcher: CoroutineDispatcher,
    onLoading: () -> Unit = {},
    noinline onError: (String) -> Unit = {},
    crossinline onComplete:  CoroutineScope.() -> Unit = {},
    crossinline execute: suspend CoroutineScope.() -> Unit
): Job where T : ViewModel, T : ExceptionParser {

    onLoading()

    return viewModelScope.launch(dispatcher) {
        try {
            execute(this)
        } catch (exception: Exception) {
            generateDisplayError(exception, onError)
        } finally {
            onComplete()
        }
    }
}

interface ExceptionParser {

    fun generateDisplayError(
        exception: Exception,
        onError: (String) -> Unit
    ) {
        when (exception) {
            is IOException -> {
                onError("No Internet")
            }

            is HttpException -> {
                onError("Server Error")
            }

            else -> {
                onError("Terjadi kesalahan")
            }
        }
    }
}