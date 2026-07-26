package com.guide.core_api.model

import kotlinx.serialization.Serializable

@Serializable
data class BloggerResponse(
    val version: String? = null,
    val encoding: String? = null,
    val feed: Feed? = null
)
