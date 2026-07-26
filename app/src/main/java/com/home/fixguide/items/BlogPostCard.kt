package com.home.fixguide.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.home.fixguide.helper.gone
import com.home.fixguide.helper.shimmerEffect

@Composable
fun BlogPostCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    title: String = "",
    category: String = "",
    imageUrl: String = "",
    authorName: String = "",
    publishedDate: String = "",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (!isLoading) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shimmerEffect(isLoading)
            ) {
                if (!isLoading) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Header image for $title",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Category Chip
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(width = 80.dp, height = 26.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .shimmerEffect(true)
                            .align(Alignment.TopStart)
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(percent = 50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                            .gone(
                                 category.isEmpty()
                            )
                    ) {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(true)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(true)
                    )
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        // Ensures the text block maintains height even if title is 1 line
                        modifier = Modifier.defaultMinSize(minHeight = 52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Row: Author and Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isLoading) Modifier.shimmerEffect(true)
                                    else Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Author Name
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .size(width = 100.dp, height = 16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .shimmerEffect(true)
                            )
                        } else {
                            Text(
                                text = authorName,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Date
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .size(width = 70.dp, height = 16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .shimmerEffect(true)
                        )
                    } else {
                        Text(
                            text = publishedDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}