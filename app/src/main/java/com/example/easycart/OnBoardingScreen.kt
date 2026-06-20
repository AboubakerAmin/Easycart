package com.example.easycart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycart.ui.theme.BorderGray
import com.example.easycart.ui.theme.EasyCartOrange
import com.example.easycart.ui.theme.SlateDark
import com.example.easycart.ui.theme.SlateLight
import com.example.easycart.ui.theme.TextMuted
import kotlinx.coroutines.delay

data class OnboardingPage(val image: Int, val title: String, val description: String)
data class MarketplaceItem(val title: String, val category: String, val price: String, val iconEmoji: String)

// ==========================================
// 🖥️ UI VIEW IMPLEMENTATIONS
// ==========================================

// 1️⃣ ONBOARDING VIEW
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(R.drawable.easylogo, "Buy & Sell Easily", "Find amazing products from trusted students around your campus."),
            OnboardingPage(R.drawable.easylogo, "Secure Shopping", "Enjoy verified sellers and secure transactions every time."),
            OnboardingPage(R.drawable.easylogo, "Everything in One Place", "Electronics, books, fashion, furniture and much more.")
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val item = pages[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(item.image),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = item.title, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SlateDark)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    fontSize = 15.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    lineHeight = 22.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(pages.size) { index ->
                val active = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(width = if (active) 18.dp else 8.dp, height = 8.dp)
                        .background(if (active) EasyCartOrange else BorderGray, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EasyCartOrange),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToGuest,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            border = BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Explore as Guest", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Preview()
@Composable
fun ShowScreen(){
    OnboardingScreen(onNavigateToLogin = {}, onNavigateToGuest = {})
}