package com.home.fixguide.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.guide.core_api.model.guide.GuideSubCategory
import com.home.fixguide.R
import com.home.fixguide.presentation.category.DetailCategoryScreen
import com.home.fixguide.presentation.component.FeatureSubCategory
import com.home.fixguide.presentation.component.FeaturedCategoryCard
import com.home.fixguide.presentation.destinations.DetailCategoryScreenDestination

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is Resource.Error -> {

            }

            Resource.Loading -> {
                CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color.Black,
                    strokeWidth = 6.dp
                )
            }

            is Resource.Success<*> -> {
                val datas =
                    (state as Resource.Success<Pair<List<GuideCategory>, List<GuideSubCategory>>>).data
                Column(modifier = Modifier.fillMaxSize() ) {

                    AsyncImage(model = R.drawable.illu_home,
                        contentDescription = "Network Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10))
                            .height(230.dp),
                        contentScale = ContentScale.Crop)

                    TabSection(selectedTabIndex){
                        selectedTabIndex = it
                    }
                    when (selectedTabIndex) {
                       0 -> {
                           CategorySection(datas.first){
                               navigator.navigate(
                                   DetailCategoryScreenDestination(
                                       guideCategory = it
                                   ))
                           }
                       }
                       else -> SubCategorySection(datas.second)
                    }
                }

            }

            else -> Unit
        }
    }


}

@Composable
private fun SubCategorySection(items: List<GuideSubCategory>) {
    LazyColumn(modifier = Modifier) {
        itemsIndexed(items) { index, item ->
            FeatureSubCategory(
                item = item,
                modifier = Modifier.clickable {
                    // Your click code here
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
        contentPadding = PaddingValues(5.dp)
    ) {
        itemsIndexed(datas) { index, item->
            FeaturedCategoryCard(
                category = item,
                onClick = {
                    onClick(item)
                },
                modifier = Modifier
                    .width(180.dp)
                    .height(180.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabSection(selectedTabIndex : Int, onClick : (Int) -> Unit){
    val list = listOf("Category", "Sub Category")
    PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
        list.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onClick(index) },
                text = { Text(title) }
            )
        }
    }
}