package com.home.fixguide.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guide.core_api.model.guide.GuideSubCategory
import com.home.fixguide.ui.theme.LightCream
import com.home.fixguide.ui.theme.TechBlue

@Composable
fun FeatureSubCategory(item: GuideSubCategory, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightCream)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
                    .background(TechBlue)
            ) {
                Text(
                    item.count,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )

            }
            Text(
                item.text,
                color = TechBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun FeatureSubCategoryPreview() {
    MaterialTheme() {
        FeatureSubCategory(item = GuideSubCategory("Computer", "1100", ""))
    }
}