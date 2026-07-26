package com.guide.core_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TypedTextValue(
    val type: String? = null,
    @SerialName("\$t")
    val text: String? = null
)
