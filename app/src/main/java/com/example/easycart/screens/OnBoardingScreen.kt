package com.example.easycart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycart.R
import com.example.easycart.model.OnboardingPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Orange = Color(0xFFFF6B00)

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit
) {

    val pages = listOf(

        OnboardingPage(
            R.drawable.easylogo,
            "Welcome to EasyCart",
            "Buy and sell products around your campus with trusted students."
        ),

        OnboardingPage(
            R.drawable.easylogo,
            "Fast Marketplace",
            "Discover books, electronics, furniture and fashion in seconds."
        ),

        OnboardingPage(
            R.drawable.easylogo,
            "Safe & Secure",
            "Verified users and secure transactions make shopping worry-free."
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    // Auto slide
    LaunchedEffect(Unit) {

        while (true) {

            delay(4000)

            val next = if (pagerState.currentPage == pages.lastIndex)
                0
            else
                pagerState.currentPage + 1

            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            TextButton(
                onClick = onNavigateToLogin
            ) {

                Text("Skip")

            }

        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val item = pages[page]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(item.image),
                    contentDescription = null,
                    modifier = Modifier.size(220.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = item.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    lineHeight = 24.sp
                )
            }

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            repeat(pages.size) { index ->

                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .height(10.dp)
                        .width(
                            if (pagerState.currentPage == index)
                                28.dp
                            else
                                10.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                Orange
                            else
                                Color.LightGray
                        )
                )

            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        if (pagerState.currentPage == pages.lastIndex) {

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                )
            ) {

                Text(
                    "Get Started",
                    color = Color.White
                )

            }

        } else {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = onNavigateToGuest
                ) {

                    Text("Guest")

                }

                Button(
                    onClick = {

                        scope.launch {

                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1
                            )

                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange
                    )
                ) {

                    Text(
                        "Next",
                        color = Color.White
                    )

                }

            }

        }

        Spacer(modifier = Modifier.height(20.dp))

    }

}