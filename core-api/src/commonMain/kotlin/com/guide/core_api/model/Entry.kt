package com.guide.core_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: TextValue? = null,
    val published: TextValue? = null,
    val updated: TextValue? = null,
    val title: TypedTextValue? = null,
    val content: TypedTextValue? = null,
    val link: List<Link>? = emptyList(),
    val author: List<Author>? = emptyList(),
    @SerialName("media\$thumbnail")
    val thumbnail: MediaThumbnail? = null,
    @SerialName("thr\$total")
    val totalComments: TextValue? = null,
    val category: List<Category>? = emptyList(),
)
