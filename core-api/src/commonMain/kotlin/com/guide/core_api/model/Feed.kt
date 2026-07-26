package com.guide.core_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val id: TextValue? = null,
    val updated: TextValue? = null,
    val category: List<Category>? = emptyList(),
    val title: TypedTextValue? = null,
    val subtitle: TypedTextValue? = null,
    val link: List<Link>? = emptyList(),
    val author: List<Author>? = emptyList(),
    val generator: Generator? = null,
    @SerialName("openSearch\$totalResults")
    val totalResults: TextValue? = null,
    @SerialName("openSearch\$startIndex")
    val startIndex: TextValue? = null,
    @SerialName("openSearch\$itemsPerPage")
    val itemsPerPage: TextValue? = null,
    val entry: List<Entry>? = emptyList()
)
