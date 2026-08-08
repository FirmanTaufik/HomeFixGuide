package com.home.fixguide.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProfileScreen(
    navigator: DestinationsNavigator
) {
    Column {
        Text("ProfileScreen, !")
        Button(onClick = { navigator.popBackStack() }) {
            Text("Kembali")
        }
    }
}