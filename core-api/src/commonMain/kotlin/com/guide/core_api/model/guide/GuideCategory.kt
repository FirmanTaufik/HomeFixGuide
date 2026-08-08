package com.guide.core_api.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class GuideCategory(
    val text : String = "",
    val image : String = "",
    val url : String = ""
)