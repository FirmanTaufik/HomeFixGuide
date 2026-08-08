package com.home.fixguide.presentation.category

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CategoryScreen(
    nama: String,
    navigator: DestinationsNavigator
) {
    Column {
        Text("Halo, $nama!")
        Button(onClick = { navigator.popBackStack() }) {
            Text("Kembali")
        }
    }
}