package com.guide.core_api.model

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val rel: String? = null,
    val type: String? = null,
    val href: String? = null,
    val title: String? = null
)
