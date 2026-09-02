package com.home.fixguide.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideDetailCategory
import com.guide.core_api.model.guide.GuideStep
import com.home.fixguide.base.BaseScreen
import com.home.fixguide.presentation.component.DetailCategoryShimmer
import com.home.fixguide.presentation.component.StepGuideShimmer
import com.home.fixguide.presentation.destinations.DetailCategoryScreenDestination
import com.home.fixguide.ui.theme.CircuitGreen
import com.home.fixguide.ui.theme.MeterYellow
import com.home.fixguide.ui.theme.TechBlue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import androidx.compose.ui.platform.LocalContext
import android.app.Activity

import com.home.fixguide.presentation.component.AdNativeView

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun DetailCategoryScreen(
    guideCategory: GuideCategory,
    viewModel: CategoryViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(guideCategory.url) {
        viewModel.getDetail(guideCategory.url)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved(guideCategory.url).collectAsStateWithLifecycle(initialValue = false)
    val nativeAdId by viewModel.nativeAdId.collectAsStateWithLifecycle()
    val nativeAdInterval by viewModel.nativeAdInterval.collectAsStateWithLifecycle()

    BaseScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel = viewModel
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = guideCategory.text.ifEmpty { "FixGuide" },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.toggleSave(
                                    url = guideCategory.url,
                                    title = guideCategory.text,
                                    imageUrl = guideCategory.image,
                                    isCurrentSaved = isSaved
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isSaved) "Saved" else "Save",
                                tint = if (isSaved) TechBlue else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is Resource.Loading -> {
                        val isGuide = guideCategory.url.contains("/Guide/", ignoreCase = true) || guideCategory.url.contains("/Wiki/", ignoreCase = true)
                        if (isGuide) {
                            StepGuideShimmer()
                        } else {
                            DetailCategoryShimmer()
                        }
                    }

                    is Resource.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = state.message.ifEmpty { "Failed to load data" },
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Button(
                                    onClick = { viewModel.getDetail(guideCategory.url) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TechBlue)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Try Again")
                                }
                            }
                        }
                    }

                    is Resource.Success<*> -> {
                        val detailData = state.data as? GuideDetailCategory
                        if (detailData != null) {
                            if (detailData.isStepGuide && detailData.listCategory.isEmpty()) {
                                // Step-by-Step Guide View (Matching Web Guide)
                                StepGuideView(
                                    detailData = detailData,
                                    fallbackCategory = guideCategory,
                                    viewModel = viewModel,
                                    activity = activity,
                                    nativeAdId = nativeAdId,
                                    nativeAdInterval = nativeAdInterval,
                                    navigator = navigator
                                )
                            } else {
                                // Device & Category Directory View (Matching Device Page)
                                DeviceDirectoryView(
                                    detailData = detailData,
                                    guideCategory = guideCategory,
                                    viewModel = viewModel,
                                    activity = activity,
                                    nativeAdId = nativeAdId,
                                    nativeAdInterval = nativeAdInterval,
                                    navigator = navigator
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

/**
 * Device / Category Directory View (Matching Web)
 */
@Composable
fun DeviceDirectoryView(
    detailData: GuideDetailCategory,
    guideCategory: GuideCategory,
    viewModel: CategoryViewModel,
    activity: Activity?,
    nativeAdId: String?,
    nativeAdInterval: Int,
    navigator: DestinationsNavigator
) {
    val subcategories = detailData.listCategory
    val guides = detailData.listGuides

    if (subcategories.isEmpty() && guides.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No devices or repair guides found.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Breadcrumbs (Home > Mac > MacBook Air)
        if (detailData.breadcrumbs.isNotEmpty()) {
            item {
                Text(
                    text = detailData.breadcrumbs.joinToString("  ›  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TechBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Header Device / Category Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (guideCategory.image.isNotBlank()) {
                        AsyncImage(
                            model = guideCategory.image,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = detailData.title.ifEmpty { guideCategory.text },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (detailData.introduction.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = detailData.introduction,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Subcategories / Device Models
        if (subcategories.isNotEmpty()) {
            item {
                Text(
                    text = "Select Device / Model",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val chunked = subcategories.chunked(2)
            itemsIndexed(chunked) { index, rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        SubCategoryItemCard(
                            item = item,
                            onClick = {
                                if (item.url.isNotBlank()) {
                                    viewModel.showInterstitialAd(activity) {
                                        navigator.navigate(
                                            DetailCategoryScreenDestination(guideCategory = item)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if ((index + 1) % nativeAdInterval == 0) {
                    AdNativeView(adUnitId = nativeAdId ?: "")
                }
            }
        }

        // Section 2: Repair Guides List
        if (guides.isNotEmpty()) {
            item {
                Text(
                    text = "Repair Guides (${guides.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            itemsIndexed(guides) { index, guideItem ->
                GuideCard(
                    item = guideItem,
                    onClick = {
                        if (guideItem.url.isNotBlank()) {
                            viewModel.showInterstitialAd(activity) {
                                navigator.navigate(
                                    DetailCategoryScreenDestination(guideCategory = guideItem)
                                )
                            }
                        }
                    }
                )
                if ((index + 1) % nativeAdInterval == 0) {
                    AdNativeView(adUnitId = nativeAdId ?: "")
                }
            }
        }
    }
}

/**
 * Step-by-Step Guide View (Matching Web)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepGuideView(
    detailData: GuideDetailCategory,
    fallbackCategory: GuideCategory,
    viewModel: CategoryViewModel,
    activity: Activity?,
    nativeAdId: String?,
    nativeAdInterval: Int,
    navigator: DestinationsNavigator
) {
    val completedSteps = remember { mutableStateMapOf<Int, Boolean>() }
    val steps = detailData.steps

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Breadcrumbs
        if (detailData.breadcrumbs.isNotEmpty()) {
            item {
                Text(
                    text = detailData.breadcrumbs.joinToString("  ›  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TechBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Header Card: Title, Difficulty, Time, Author
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = detailData.title.ifEmpty { fallbackCategory.text },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (detailData.author.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Author: ${detailData.author}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta Chips (Difficulty, Time, Steps)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (detailData.difficulty.isNotBlank()) {
                            item {
                                MetaChip(
                                    label = "Difficulty: ${detailData.difficulty}",
                                    badgeColor = MeterYellow.copy(alpha = 0.2f),
                                    textColor = Color(0xFFB7791F)
                                )
                            }
                        }
                        if (detailData.timeRequired.isNotBlank()) {
                            item {
                                MetaChip(
                                    label = "Time: ${detailData.timeRequired}",
                                    badgeColor = TechBlue.copy(alpha = 0.15f),
                                    textColor = TechBlue
                                )
                            }
                        }
                        item {
                            MetaChip(
                                label = "${steps.size} Steps",
                                badgeColor = CircuitGreen.copy(alpha = 0.15f),
                                textColor = CircuitGreen
                            )
                        }
                    }

                    // Summary / Intro
                    if (detailData.contentSummary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = detailData.contentSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Tools & Parts Section
        if (detailData.tools.isNotEmpty() || detailData.parts.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (detailData.tools.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = TechBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tools Required:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                detailData.tools.forEach { tool ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                            RoundedCornerShape(6.dp)
                                        )
                                    ) {
                                        Text(
                                            text = tool,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step-by-Step Section Header
        item {
            Text(
                text = "Repair Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Step Cards
        itemsIndexed(steps) { index, step ->
            val isChecked = completedSteps[step.stepNumber] ?: false
            StepCard(
                step = step,
                isChecked = isChecked,
                onToggleCheck = {
                    completedSteps[step.stepNumber] = !isChecked
                }
            )
            if ((index + 1) % nativeAdInterval == 0) {
                AdNativeView(adUnitId = nativeAdId ?: "")
            }
        }

        // Related Guides
        if (detailData.listGuides.isNotEmpty()) {
            item {
                Text(
                    text = "Related Guides",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            itemsIndexed(detailData.listGuides) { index, guideItem ->
                GuideCard(
                    item = guideItem,
                    onClick = {
                        viewModel.showInterstitialAd(activity) {
                            navigator.navigate(
                                DetailCategoryScreenDestination(guideCategory = guideItem)
                            )
                        }
                    }
                )
                if ((index + 1) % nativeAdInterval == 0) {
                    AdNativeView(adUnitId = nativeAdId ?: "")
                }
            }
        }
    }
}

/**
 * Step Card
 */
@Composable
fun StepCard(
    step: GuideStep,
    isChecked: Boolean,
    onToggleCheck: () -> Unit
) {
    val bulletColors = listOf(
        Color(0xFFE53935), // Red
        Color(0xFFFF9800), // Orange
        Color(0xFFFFD600), // Yellow
        Color(0xFF43A047), // Green
        Color(0xFF1E88E5)  // Blue
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar: Step Number + Title + Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isChecked) CircuitGreen else TechBlue,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${step.stepNumber}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = step.title.ifEmpty { "Step ${step.stepNumber}" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onToggleCheck,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isChecked) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = "Completed",
                        tint = if (isChecked) CircuitGreen else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Step Images
            if (step.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                step.images.forEach { imgUrl ->
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bullet Point Instructions
            if (step.lines.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                step.lines.forEachIndexed { lineIdx, line ->
                    val colorDot = bulletColors[lineIdx % bulletColors.size]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp, end = 10.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colorDot)
                        )
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

/**
 * Guide Item Card
 */
@Composable
fun GuideCard(
    item: GuideCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            if (item.image.isNotBlank()) {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.text,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TechBlue.copy(alpha = 0.15f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = TechBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Step-by-step Repair Guide",
                    style = MaterialTheme.typography.bodySmall,
                    color = TechBlue
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Sub-Category / Device Item Card
 */
@Composable
fun SubCategoryItemCard(
    item: GuideCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                if (item.image.isNotBlank()) {
                    AsyncImage(
                        model = item.image,
                        contentDescription = item.text,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TechBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.text,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MetaChip(
    label: String,
    badgeColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = badgeColor,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun DetailCategoryScreenPreview() {
    MaterialTheme {
        Surface {
            SubCategoryItemCard(
                item = GuideCategory(
                    text = "MacBook Pro",
                    image = "",
                    url = "/Device/MacBook_Pro"
                ),
                onClick = {}
            )
        }
    }
}
