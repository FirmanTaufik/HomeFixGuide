package com.home.fixguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.android.gms.ads.MobileAds
import com.home.fixguide.data.local.ThemeManager
import com.home.fixguide.presentation.NavGraphs
import com.home.fixguide.presentation.appCurrentDestinationAsState
import com.home.fixguide.presentation.destinations.HomeScreenDestination
import com.home.fixguide.presentation.destinations.ProfileScreenDestination
import com.home.fixguide.presentation.destinations.SavedScreenDestination
import com.home.fixguide.presentation.destinations.SplashScreenDestination
import com.home.fixguide.ui.components.AdBannerView
import com.home.fixguide.ui.theme.HomeFixGuideTheme
import com.home.fixguide.ui.theme.TechBlue
import com.home.fixguide.ui.theme.TechBlueLight
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this)

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val bannerAdId by mainViewModel.bannerAdId.collectAsStateWithLifecycle()

            val isSystemDark = isSystemInDarkTheme()
            val userThemePreference by themeManager.isDarkMode.collectAsStateWithLifecycle()
            val isDark = userThemePreference ?: isSystemDark

            val items = listOf(
                Triple("Home", Icons.Default.Home, HomeScreenDestination),
                Triple("Saved", Icons.Default.Bookmark, SavedScreenDestination),
                Triple("Profile", Icons.Default.Person, ProfileScreenDestination)
            )
            val engine = rememberNavHostEngine()
            val navController = engine.rememberNavController()

            var selectedDestination by rememberSaveable {
                mutableIntStateOf(0)
            }

            val mapListScreenMenu = items.map { it.third }
            val currentDestination by navController.appCurrentDestinationAsState()

            val showBottomMenu = currentDestination in mapListScreenMenu
            val isSplashScreen = currentDestination == SplashScreenDestination
            val showBanner = !isSplashScreen && !bannerAdId.isNullOrBlank()

            HomeFixGuideTheme(darkTheme = isDark) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (showBottomMenu || showBanner) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 6.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                ) {
                                    AnimatedVisibility(showBottomMenu) {
                                        NavigationBar(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            tonalElevation = 0.dp,
                                            windowInsets = WindowInsets(0, 0, 0, 0)
                                        ) {
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
                                                    label = {
                                                        Text(
                                                            text = destination.first,
                                                            fontWeight = if (selectedDestination == index) FontWeight.Bold else FontWeight.Normal
                                                        )
                                                    },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        selectedIconColor = TechBlue,
                                                        selectedTextColor = TechBlue,
                                                        indicatorColor = TechBlueLight,
                                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    if (showBanner && !bannerAdId.isNullOrBlank()) {
                                        AdBannerView(adUnitId = bannerAdId!!)
                                    }
                                }
                            }
                        }
                    }) { innerPadding ->
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        engine = engine,
                        navController = navController,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    )
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
}