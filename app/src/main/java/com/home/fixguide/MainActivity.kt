package com.home.fixguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guide.core_api.Resource
import com.guide.core_api.model.BloggerResponse
import com.guide.core_api.model.User
import com.home.fixguide.helper.toLocalDeviceFormat
import com.home.fixguide.items.BlogPostCard
import com.home.fixguide.ui.theme.HomeFixGuideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewModel : MainViewModel = hiltViewModel()

            HomeFixGuideTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        LaunchedEffect(Unit) {
                            viewModel.getUser()
                        }
                        when(val currentState =uiState){
                            is Resource.Error ->  {
                                val message = currentState.message
                                Column(modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(message)
                                    Button(onClick = {
                                        viewModel.getUser()
                                    }) {
                                        Text("Try Again")
                                    }
                                }
                            }
                            Resource.Idle -> Unit
                            Resource.Loading ->  {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(10){
                                        BlogPostCard(isLoading = true) { }
                                    }
                                }
                            }
                            Resource.SessionExpired ->  {
                                Text("Navigate To Logout")
                            }
                            is Resource.Success<*> -> {
                                val datas = currentState.data as  BloggerResponse
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    itemsIndexed(datas.feed?.entry ?: emptyList()){ index, item ->
                                        var category = ""
                                        if (!item.category.isNullOrEmpty()) {
                                            category = item.category?.first()?.term ?: ""
                                        }
                                        BlogPostCard(
                                            title = item.title?.text ?: "",
                                            category = category,
                                            imageUrl = item.thumbnail?.url ?: "",
                                            authorName= item.author?.first()?.name?.text ?: "",
                                            publishedDate = item.published?.text?.toLocalDeviceFormat() ?: "",

                                        ){

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeFixGuideTheme {
        Greeting("Android")
    }
}