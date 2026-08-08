package com.home.fixguide.presentation.splash


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.home.fixguide.presentation.destinations.CategoryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator
) {
    // Tambahkan Modifier.fillMaxSize() di sini
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Beri warna background untuk testing
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Agar teks ke tengah layar
    ) {
        Text("Ini Home Screen", color = Color.Black)
        Button(
            onClick = {
                navigator.navigate(CategoryScreenDestination(nama = "Developer"))
            }
        ) {
            Text("Ke Halaman Category")
        }
    }

}