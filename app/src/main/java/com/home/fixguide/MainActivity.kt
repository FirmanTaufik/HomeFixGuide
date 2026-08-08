package com.home.fixguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.guide.core_api.Resource
import com.guide.core_api.model.BloggerResponse
import com.home.fixguide.helper.toLocalDeviceFormat
import com.home.fixguide.items.BlogPostCard
import com.home.fixguide.presentation.NavGraphs
import com.home.fixguide.presentation.appCurrentDestinationAsState
import com.home.fixguide.presentation.destinations.CategoryScreenDestination
import com.home.fixguide.presentation.destinations.HomeScreenDestination
import com.home.fixguide.presentation.destinations.ProfileScreenDestination
import com.home.fixguide.ui.theme.HomeFixGuideTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.utils.navGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val items = listOf(
                Triple("Home", Icons.Default.Home, HomeScreenDestination),
                Triple("Profile", Icons.Default.Person, ProfileScreenDestination)
            )
            val engine = rememberNavHostEngine()
            val navController = engine.rememberNavController()


            var selectedDestination by rememberSaveable {
                mutableIntStateOf(0)
            }

            val mapListScreenMenu = items.map { it.third }
            val currentDestination by navController.appCurrentDestinationAsState()

            val showBottomMenu = currentDestination in  mapListScreenMenu

                HomeFixGuideTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AnimatedVisibility(showBottomMenu) {
                                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                    items.forEachIndexed { index, destination ->
                                        NavigationBarItem(
                                            selected = selectedDestination == index,
                                            onClick = {
                                                navController.navigate(destination.third.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                                selectedDestination = index
                                            },
                                            icon = {
                                                Icon(
                                                    destination.second,
                                                    contentDescription = destination.first
                                                )
                                            },
                                            label = { Text(destination.first) }
                                        )
                                    }
                                }
                            }
                        }) { innerPadding ->
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            engine = engine,
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ListItems(innerPadding: PaddingValues) {
        val viewModel: MainViewModel = hiltViewModel()

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
            when (val currentState = uiState) {
                is Resource.Error -> {
                    val message = currentState.message
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(message)
                        Button(onClick = {
                            viewModel.getUser()
                        }) {
                            Text("Try Again")
                        }
                    }
                }

                Resource.Idle -> Unit
                Resource.Loading -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(10) {
                            BlogPostCard(isLoading = true) { }
                        }
                    }
                }

                Resource.SessionExpired -> {
                    Text("Navigate To Logout")
                }

                is Resource.Success<*> -> {
                    val datas = currentState.data as BloggerResponse
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(datas.feed?.entry ?: emptyList()) { index, item ->
                            var category = ""
                            if (!item.category.isNullOrEmpty()) {
                                category = item.category?.first()?.term ?: ""
                            }
                            BlogPostCard(
                                title = item.title?.text ?: "",
                                category = category,
                                imageUrl = item.thumbnail?.url ?: "",
                                authorName = item.author?.first()?.name?.text ?: "",
                                publishedDate = item.published?.text?.toLocalDeviceFormat() ?: "",

                                ) {

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