
package com.example.easycart.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.easycart.screens.*
import com.example.easycart.viewmodel.AuthViewModel
import com.example.easycart.auth.GoogleAuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Main : Screen("main")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
    object Payment : Screen("payment")
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val auth = remember { FirebaseAuth.getInstance() }
    val authViewModel = remember { AuthViewModel() }

    val authState = remember { mutableStateOf(auth.currentUser) }

    // ── App-level state ────────────────────────────────────────────────────
    val cartMap = remember { mutableStateMapOf<String, CartItem>() }
    val cartItems by remember { derivedStateOf { cartMap.values.toList() } }
    val likedProducts = remember { mutableStateSetOf<String>() }

    // ── Firebase auth listener ─────────────────────────────────────────────
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            authState.value = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // ── Cart helpers ───────────────────────────────────────────────────────
    fun addToCart(product: Product) {
        val existing = cartMap[product.id]
        if (existing != null) {
            cartMap[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            cartMap[product.id] = CartItem(product = product, quantity = 1)
        }
    }

    fun removeFromCart(productId: String) {
        cartMap.remove(productId)
    }

    fun updateQty(productId: String, newQty: Int) {
        val existing = cartMap[productId] ?: return
        if (newQty <= 0) cartMap.remove(productId)
        else cartMap[productId] = existing.copy(quantity = newQty)
    }

    fun toggleLike(productId: String) {
        if (likedProducts.contains(productId)) likedProducts.remove(productId)
        else likedProducts.add(productId)
    }

    // ── Navigation ─────────────────────────────────────────────────────────
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ─── SPLASH ───────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen()
            LaunchedEffect(authState.value) {
                delay(2500)
                if (authState.value == null) {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        // ─── ONBOARDING ───────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {

            // KEY FIX: watch authState here so when signInAnonymously() resolves
            // and Firebase updates the current user, we navigate immediately.
            LaunchedEffect(authState.value) {
                if (authState.value != null) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            }

            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.route)
                },
                onNavigateToGuest = {
                    // signInAnonymously is async; the LaunchedEffect above
                    // handles navigation once Firebase confirms the sign-in.
                    auth.signInAnonymously()
                }
            )
        }

        // ─── AUTH ─────────────────────────────────────────────────────────
        composable(Screen.Auth.route) {
            val context = androidx.compose.ui.platform.LocalContext.current

            // KEY FIX: same watcher for the Auth screen's "Continue as Guest" button
            LaunchedEffect(authState.value) {
                if (authState.value != null) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            }

            AuthScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // Handled by the LaunchedEffect above reacting to authState change
                },
                onGuest = {
                    auth.signInAnonymously()
                    // Navigation handled by LaunchedEffect above
                },
                onGoogleClick = {
                    authViewModel.loginWithGoogle(
                        googleAuthManager = GoogleAuthManager(context),
                        onSuccess = {
                            // Also handled by LaunchedEffect, but onSuccess is kept
                            // as a safety fallback
                        }
                    )
                }
            )
        }

        // ─── MAIN (tabs: Home / Cart / Profile) ───────────────────────────
        composable(Screen.Main.route) {
            val user = authState.value

            if (user == null) {
                LaunchedEffect(true) {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
                return@composable
            }

            MainScreen(
                username = user.displayName ?: user.email?.substringBefore("@") ?: "Guest",
                isGuest = user.isAnonymous,
                cartItems = cartItems,
                likedProducts = likedProducts,
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.route)
                },
                onLogout = {
                    auth.signOut()
                    cartMap.clear()
                    likedProducts.clear()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.createRoute(product.id))
                },
                onToggleLike = { productId -> toggleLike(productId) },
                onAddToCart = { product -> addToCart(product) },
                onRemoveFromCart = { productId -> removeFromCart(productId) },
                onUpdateQty = { productId, qty -> updateQty(productId, qty) },
                onBuyNow = { product -> addToCart(product) },
                onCheckout = {
                    navController.navigate(Screen.Payment.route)
                }
            )
        }

        // ─── PRODUCT DETAIL ───────────────────────────────────────────────
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val product = sampleProducts.find { it.id == productId } ?: return@composable
            val user = authState.value

            ProductDetailScreen(
                product = product,
                isGuest = user?.isAnonymous ?: true,
                isLiked = likedProducts.contains(productId),
                onBack = { navController.popBackStack() },
                onToggleLike = { toggleLike(productId) },
                onAddToCart = { p -> addToCart(p) },
                onBuyNow = { p ->
                    addToCart(p)
                    navController.navigate(Screen.Payment.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.route)
                }
            )
        }

        // ─── PAYMENT ──────────────────────────────────────────────────────
        composable(Screen.Payment.route) {
            PaymentScreen(
                cartItems = cartItems,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    cartMap.clear()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                    }
                }
            )
        }
    }
}