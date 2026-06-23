package com.example.easycart.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private val Orange = Color(0xFFFF6B00)

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun MainScreen(
    username: String,
    isGuest: Boolean,
    cartItems: List<CartItem>,
    likedProducts: Set<String>,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit,
    onProductClick: (Product) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onUpdateQty: (String, Int) -> Unit,
    onBuyNow: (Product) -> Unit,
    onCheckout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    val navItems = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("My Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, cartItems.size),
        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount > 0 && !isGuest) {
                                        Badge(
                                            containerColor = Orange
                                        ) {
                                            Text("${item.badgeCount}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Orange,
                            selectedTextColor = Orange,
                            indicatorColor = Orange.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    isGuest = isGuest,
                    likedProducts = likedProducts,
                    onProductClick = onProductClick,
                    onToggleLike = onToggleLike,
                    onAddToCart = { product ->
                        if (isGuest) {
                            // handled inside via dialog
                        } else {
                            onAddToCart(product)
                        }
                    },
                    onBuyNow = onBuyNow,
                    onNavigateToLogin = onNavigateToLogin
                )
                1 -> CartScreen(
                    isGuest = isGuest,
                    cartItems = cartItems,
                    onNavigateToLogin = onNavigateToLogin,
                    onRemoveFromCart = onRemoveFromCart,
                    onUpdateQty = onUpdateQty,
                    onCheckout = onCheckout
                )
                2 -> ProfileScreen(
                    username = username,
                    isGuest = isGuest,
                    onNavigateToLogin = onNavigateToLogin,
                    onLogout = onLogout
                )
            }
        }
    }
}
