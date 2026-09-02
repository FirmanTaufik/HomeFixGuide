package com.home.fixguide.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideSubCategory
import com.home.fixguide.base.BaseScreen
import com.home.fixguide.presentation.component.FeatureSubCategory
import com.home.fixguide.presentation.component.FeaturedCategoryCard
import com.home.fixguide.presentation.component.HomeCategoryGridShimmer
import com.home.fixguide.presentation.component.HomeSubCategoryListShimmer
import com.home.fixguide.presentation.component.SearchListShimmer
import com.home.fixguide.presentation.destinations.DetailCategoryScreenDestination
import com.home.fixguide.ui.theme.CircuitGreen
import com.home.fixguide.ui.theme.IndigoAccent
import com.home.fixguide.ui.theme.TechBlue
import com.home.fixguide.ui.theme.TechBlueLight
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.home.fixguide.ui.theme.MeterYellow

import androidx.compose.ui.platform.LocalContext
import android.app.Activity

@Destination
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) = with(viewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val state by uiState.collectAsStateWithLifecycle()
    val searchResults by searchState.collectAsStateWithLifecycle()
    val currentQuery by searchQuery.collectAsStateWithLifecycle()
    val userThemePreference by isDarkMode.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val isDark = userThemePreference ?: isSystemDark
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    BaseScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel = viewModel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 16.dp, end = 16.dp, top = 2.dp)
        ) {
            // Modern Header Branding with Dark Mode Toggle
            HomeHeader(
                isDark = isDark,
                onToggleTheme = { viewModel.toggleTheme(isSystemDark) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Modern Search Bar
            OutlinedTextField(
                value = currentQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        text = "Search devices or repair guides...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TechBlue
                    )
                },
                trailingIcon = {
                    if (currentQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            if (currentQuery.isNotBlank()) {
                // Search Results View
                when (val resultState = searchResults) {
                    is Resource.Loading -> {
                        SearchListShimmer()
                    }

                    is Resource.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = resultState.message.ifEmpty { "Search failed." },
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    is Resource.Success<*> -> {
                        val items = (resultState.data as? List<*>)?.filterIsInstance<GuideCategory>() ?: emptyList()
                        if (items.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Text(
                                        text = "No results found for \"$currentQuery\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Search Results",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = TechBlueLight
                                        ) {
                                            Text(
                                                text = "${items.size}",
                                                color = TechBlue,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                items(items) { searchItem ->
                                    SearchResultCard(
                                        item = searchItem,
                                        onClick = {
                                            if (searchItem.url.isNotBlank()) {
                                                showInterstitialAd(activity) {
                                                    navigator.navigate(
                                                        DetailCategoryScreenDestination(guideCategory = searchItem)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            } else {
                // Normal Home View (Category Tabs & Grid)
                when (val homeState = state) {
                    is Resource.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Failed to load categories. Please check your internet connection.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Resource.Loading -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabSection(
                                selectedTabIndex = selectedTabIndex,
                                onClick = { selectedTabIndex = it }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (selectedTabIndex == 0) {
                                HomeCategoryGridShimmer()
                            } else {
                                HomeSubCategoryListShimmer()
                            }
                        }
                    }

                    is Resource.Success<*> -> {
                        val data = homeState.data as? Pair<List<GuideCategory>, List<GuideSubCategory>>
                        val categories = data?.first ?: emptyList()
                        val subcategories = data?.second ?: emptyList()

                        Column(modifier = Modifier.fillMaxSize()) {
                            TabSection(
                                selectedTabIndex = selectedTabIndex,
                                onClick = { selectedTabIndex = it }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (selectedTabIndex == 0) {
                                CategorySection(
                                    datas = categories,
                                    onClick = { item ->
                                        showInterstitialAd(activity) {
                                            navigator.navigate(
                                                DetailCategoryScreenDestination(guideCategory = item)
                                            )
                                        }
                                    }
                                )
                            } else {
                                SubCategorySection(
                                    items = subcategories,
                                    onClick = { item ->
                                        showInterstitialAd(activity) {
                                            navigator.navigate(
                                                DetailCategoryScreenDestination(
                                                    guideCategory = GuideCategory(
                                                        text = item.text,
                                                        image = "",
                                                        url = item.url
                                                    )
                                                )
                                            )
                                        }
                                    }
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

@Composable
private fun HomeHeader(
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = TechBlue,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "FixGuide",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Repair everything yourself",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Theme Toggle Icon Button
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(40.dp)
        ) {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = if (isDark) MeterYellow else TechBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    item: GuideCategory,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp, pressedElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TechBlueLight,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = TechBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                val isGuide = item.url.contains("/Guide/")
                val typeLabel = if (isGuide) "Repair Guide" else "Device / Model"
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = if (isGuide) CircuitGreen else TechBlue
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SubCategorySection(
    items: List<GuideSubCategory>,
    onClick: (GuideSubCategory) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items) { _, item ->
            FeatureSubCategory(
                item = item,
                modifier = Modifier.clickable {
                    onClick(item)
                }
            )
        }
    }
}

@Composable
private fun CategorySection(datas: List<GuideCategory>, onClick: (GuideCategory) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(2.dp)
    ) {
        itemsIndexed(datas) { _, item ->
            FeaturedCategoryCard(
                category = item,
                onClick = {
                    onClick(item)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSection(selectedTabIndex: Int, onClick: (Int) -> Unit) {
    val list = listOf("Categories", "All Subcategories")
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = TechBlue,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                color = TechBlue,
                width = 48.dp,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            )
        },
        divider = {}
    ) {
        list.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onClick(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (selectedTabIndex == index) TechBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}