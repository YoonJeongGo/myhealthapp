package com.healthlog.myapplication1.ui.screen.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: (hasProfile: Boolean) -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "fade"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Text(
                text = "Health",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Log",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = GreenAccent
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "나만의 건강 기록",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        onFinish(false) // AppNavigation에서 hasProfile 체크
    }
}
