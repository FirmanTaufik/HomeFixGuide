package com.guide.core_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val name: TextValue? = null,
    val uri: TextValue? = null,
    val email: TextValue? = null,
    @SerialName("gd\$image")
    val image: AuthorImage? = null
)
