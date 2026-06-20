package com.example.easycart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import com.example.easycart.ui.theme.EasycartTheme
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.dotlottie.dlplayer.Mode

// ===============================
// AUTH STATE
// ===============================
sealed class AuthState {
    object Loading : AuthState()
    object Guest : AuthState()
    data class LoggedIn(val name: String) : AuthState()
}

// ===============================
// MAIN ACTIVITY
// ===============================
class MainActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EasyCartApp(auth)
        }
    }
}

// ===============================
// ROOT APP
// ===============================
@Composable
fun EasyCartApp(auth: FirebaseAuth) {

    var state by remember { mutableStateOf<AuthState>(AuthState.Loading) }

    LaunchedEffect(Unit) {
        delay(1000)

        val user = auth.currentUser
        state = if (user == null) AuthState.Guest
        else AuthState.LoggedIn(user.displayName ?: "Customer")
    }

    EasyCartAppContent(
        state = state,
        onGuest = {
            auth.signInAnonymously()
            state = AuthState.LoggedIn("Guest User")
        },
        onLoginSuccess = {
            val user = auth.currentUser
            state = AuthState.LoggedIn(user?.displayName ?: "Student")
        },
        onLogout = {
            auth.signOut()
            state = AuthState.Guest
        }
    )
}

@Composable
fun EasyCartAppContent(
    state: AuthState,
    onGuest: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    when (state) {

        AuthState.Loading -> SplashScreen()

        AuthState.Guest -> AuthScreen(
            onGuest = onGuest,
            onLoginSuccess = onLoginSuccess
        )

        is AuthState.LoggedIn -> MainScreen(
            username = (state as AuthState.LoggedIn).name,
            onLogout = onLogout
        )
    }
}

// ===============================
// SPLASH
// ===============================
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DotLottieAnimation(
            source = DotLottieSource.Asset("Shopping Loader.json"),
            autoplay = true,
            loop = true,
            speed = 3f,
            useFrameInterpolation = false,
            playMode = Mode.FORWARD,
            modifier = Modifier.size(500.dp).background(Color.LightGray)
        )
    }
}

// ===============================
// AUTH SCREEN
// ===============================
@Composable
fun AuthScreen(
    onGuest: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("EasyCart", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(30.dp))

        Button(onClick = {
            // TODO: Implement Google Sign-In properly (Firebase Auth)
            onLoginSuccess()
        }) {
            Text("Login with Google")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = onGuest) {
            Text("Continue as Guest")
        }
    }
}

// ===============================
// MAIN SCREEN
// ===============================
@Composable
fun MainScreen(username: String, onLogout: () -> Unit) {

    var tab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {

        when (tab) {
            0 -> HomeScreen(username)
            1 -> CartScreen()
            2 -> ProfileScreen(username, onLogout)
        }

        NavigationBar {

            NavigationBarItem(
                selected = tab == 0,
                onClick = { tab = 0 },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") }
            )

            NavigationBarItem(
                selected = tab == 1,
                onClick = { tab = 1 },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                label = { Text("Cart") }
            )

            NavigationBarItem(
                selected = tab == 2,
                onClick = { tab = 2 },
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Profile") }
            )
        }
    }
}

// ===============================
// HOME SCREEN (IMAGE SLIDER)
// ===============================
@Composable
fun HomeScreen(username: String) {

    val images = listOf(
        "https://images.unsplash.com/photo-1581091870622-3c1f8f1f1a1f",
        "https://images.unsplash.com/photo-1522202176988-66273c2fd55f",
        "https://images.unsplash.com/photo-1517430816045-df4b7de11d1d"
    )

    val pagerState = rememberPagerState(pageCount = { images.size })

    // Auto-slide every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val next = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {

        Text(
            "Hello $username 👋",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Welcome to EasyCart Marketplace")

        Spacer(Modifier.height(16.dp))

        // IMAGE CAROUSEL
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->

            Card(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = images[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        val items = listOf(
            "📚 Calculus Book - MK 15,000",
            "💻 HP Laptop - MK 320,000",
            "💡 Study Lamp - MK 12,000"
        )

        items.forEach {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(it, modifier = Modifier.padding(12.dp))
            }
        }
    }
}

// ===============================
// CART
// ===============================
@Composable
fun CartScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cart is empty 🛒")
    }
}

// ===============================
// PROFILE
// ===============================
@Composable
fun ProfileScreen(username: String, onLogout: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Text("Name: $username")

        Spacer(Modifier.height(20.dp))

        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}

@Preview()
@Composable()
fun showscree(){
    EasycartTheme() {
        HomeScreen(username = "mada")
    }
}

@Preview(showBackground = true, name = "App Loading")
@Composable
fun EasyCartAppLoadingPreview() {
    EasycartTheme {
        EasyCartAppContent(state = AuthState.Loading)
    }
}

@Preview(showBackground = true, name = "App Guest")
@Composable
fun EasyCartAppGuestPreview() {
    EasycartTheme {
        EasyCartAppContent(state = AuthState.Guest)
    }
}

@Preview(showBackground = true, name = "App LoggedIn")
@Composable
fun EasyCartAppLoggedInPreview() {
    EasycartTheme {
        EasyCartAppContent(state = AuthState.LoggedIn("John Doe"))
    }
}
