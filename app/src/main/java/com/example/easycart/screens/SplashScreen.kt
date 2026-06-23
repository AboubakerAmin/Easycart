package com.example.easycart.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycart.R
import com.dotlottie.dlplayer.Mode
import com.example.easycart.ui.theme.EasyCartRed
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource

@Composable
fun SplashScreen() {

    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )

        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.easylogo),
            contentDescription = "EasyCart Logo",
            modifier = Modifier
                .size(140.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "EasyCart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(alpha.value)
        )

        Spacer(modifier = Modifier.height(30.dp))

        DotLottieAnimation(
            source = DotLottieSource.Asset("Shopping Loader.json"),
            autoplay = true,
            loop = true,
            playMode = Mode.FORWARD,
            speed = 2f,
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Buy • Sell • Discover",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}