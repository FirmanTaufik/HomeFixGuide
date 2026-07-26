package com.guide.core_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Generator(
    val version: String? = null,
    val uri: String? = null,
    @SerialName("\$t")
    val text: String? = null
)
