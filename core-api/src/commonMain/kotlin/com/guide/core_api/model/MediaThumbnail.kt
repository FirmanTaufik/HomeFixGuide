package com.guide.core_api.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaThumbnail(
    val url: String? = null,
    val height: String? = null,
    val width: String? = null
)
