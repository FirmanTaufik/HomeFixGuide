package com.guide.core_api.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class GuideStep(
    val stepNumber: Int = 1,
    val title: String = "",
    val lines: List<String> = emptyList(),
    val images: List<String> = emptyList()
)

@Serializable
data class GuideDetailCategory (
    var title: String = "",
    var introduction: String = "",
    var author: String = "",
    var difficulty: String = "",
    var timeRequired: String = "",
    var tools: List<String> = emptyList(),
    var parts: List<String> = emptyList(),
    var breadcrumbs: List<String> = emptyList(),
    var listCategory: List<GuideCategory> = emptyList(),
    var listGuides: List<GuideCategory> = emptyList(),
    var contentSummary: String = "",
    var steps: List<GuideStep> = emptyList(),
    var isStepGuide: Boolean = false
)