package com.home.fixguide.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.home.fixguide.presentation.destinations.HomeScreenDestination
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.home.fixguide.presentation.destinations.SplashScreenDestination
import com.home.fixguide.ui.theme.CircuitGreen
import com.home.fixguide.ui.theme.IndigoAccent
import com.home.fixguide.ui.theme.TechBlue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()

    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }
    val textAlpha = remember { Animatable(0f) }
    val bottomAlpha = remember { Animatable(0f) }

    // Pulsing background ambient glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(key1 = true) {
        // Staggered Entrance Animations
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(350)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }

        delay(300)
        launch {
            bottomAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }

        // Navigate to HomeScreen after splash display
        delay(1400)
        navigator.navigate(HomeScreenDestination) {
            popUpTo(SplashScreenDestination.route) {
                inclusive = true
            }
        }
    }

    // Dynamic Colors based on Theme
    val bgGradient = if (isDark) {
        listOf(
            Color(0xFF0B1120),
            Color(0xFF030712),
            Color(0xFF020617)
        )
    } else {
        listOf(
            Color(0xFFEFF6FF),
            Color(0xFFE0E7FF),
            Color(0xFFF8FAFC)
        )
    }

    val glowColors = if (isDark) {
        listOf(
            TechBlue.copy(alpha = 0.35f),
            IndigoAccent.copy(alpha = 0.15f),
            Color.Transparent
        )
    } else {
        listOf(
            TechBlue.copy(alpha = 0.22f),
            IndigoAccent.copy(alpha = 0.10f),
            Color.Transparent
        )
    }

    val titleColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
    val badgeBg = if (isDark) Color(0xFF1E293B).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f)
    val badgeBorder = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val badgeTextColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = bgGradient)),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Ambient Light Background
        Box(
            modifier = Modifier
                .size(240.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = glowColors))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated App Logo Container
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .rotate(rotation.value)
                    .size(100.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                TechBlue,
                                IndigoAccent
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title & Tagline with Staggered Fade-in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                Text(
                    text = "FixGuide",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Repair everything yourself",
                    style = MaterialTheme.typography.bodyLarge,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Bottom Badge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 44.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(bottomAlpha.value)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = badgeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeBorder),
                    shadowElevation = if (isDark) 0.dp else 2.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CircuitGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Powered by FixGuide",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = badgeTextColor
                        )
                    }
                }
            }
        }
    }
}