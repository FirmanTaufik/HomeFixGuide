package com.home.fixguide.presentation.category

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideDetailCategory
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun DetailCategoryScreen(
    guideCategory: GuideCategory ,
    viewModel: CategoryViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) = with(viewModel){
    LaunchedEffect(Unit) {
        viewModel.getDetail(guideCategory.url)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {
                Text(guideCategory.text)
            },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.popBackStack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            }
        )
    }) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()
            .padding(paddingValues),
            contentAlignment = Alignment.Center) {

            when(uiState){
                is Resource.Error -> {

                }
                Resource.Loading -> CircularProgressIndicator(
                    color = Color.Blue
                )
                is Resource.Success<*> -> {
                    val data =  (uiState as Resource.Success<Any?>).data as GuideDetailCategory
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(data.listCategory){ index, item ->
                            Text(item.text)
                        }
                    }
                }
                else -> Unit
            }

        }
    }
}


@Composable
@Preview(showSystemUi = true)
fun DetailCategoryScreenPreview(){
    MaterialTheme() {
        DetailCategoryScreen(
            GuideCategory(),
            viewModel = hiltViewModel<CategoryViewModel>(),
            navigator = EmptyDestinationsNavigator
        )
    }
}