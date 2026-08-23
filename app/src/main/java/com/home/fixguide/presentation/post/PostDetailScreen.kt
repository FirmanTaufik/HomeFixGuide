package com.home.fixguide.presentation.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.home.fixguide.ui.theme.HomeFixGuideTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun PostDetailScreen(
    title: String,
    imageUrl: String,
    content: String, // Nanti bisa disesuaikan jika berupa HTML
    author: String,
    date: String,
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Post", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = author, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(text = "•", style = MaterialTheme.typography.labelLarge)
                    Text(text = date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PostDetailScreenPreview() {
    HomeFixGuideTheme {
        PostDetailScreen(
            title = "Panduan Memperbaiki Baterai Ponsel yang Cepat Habis",
            imageUrl = "https://images.unsplash.com/photo-1588508065123-287b28e013da",
            content = "Langkah pertama dalam mengatasi baterai yang cepat habis adalah dengan memeriksa kesehatan baterai di menu Pengaturan. Jika kapasitas maksimum sudah di bawah 80%, pertimbangkan untuk mengganti baterai dengan yang baru. Pastikan selalu menggunakan obeng presisi dan alat pembuka berbahan plastik agar tidak merusak sasis maupun layar perangkat.",
            author = "Admin FixGuide",
            date = "23 Agustus 2026",
            navigator = EmptyDestinationsNavigator
        )
    }
}