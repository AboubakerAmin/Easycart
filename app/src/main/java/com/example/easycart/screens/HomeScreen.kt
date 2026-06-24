package com.example.easycart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycart.R

private val Orange = Color(0xFFFF6B00)
private val OrangeSoft = Color(0xFFFFF3EB)
private val DarkText = Color(0xFF1A1A2E)
private val GrayText = Color(0xFF9E9E9E)

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val originalPrice: String = "",
    val rating: Float = 4.5f,
    val reviewCount: Int = 128,
    val soldCount: String = "1.2k sold",
    val image: Int,
    val images: List<Int> = emptyList(),
    val description: String = "",
    val supplier: String = "EasyCart Official Store",
    val minOrder: String = "1 piece",
    val deliveryTime: String = "3-7 days"
)

val sampleProducts = listOf(
    Product(
        "1", "Calculus Textbook", "Books", "MK 15,000", "MK 20,000",
        4.8f, 342, "890 sold", R.drawable.easylogo,
        description = "Complete calculus textbook covering differential and integral calculus, sequences, series, and multivariable calculus. Perfect for university students.",
        supplier = "Academic Books Store", minOrder = "1 piece", deliveryTime = "2-4 days"
    ),
    Product(
        "2", "HP Laptop 15.6\"", "Electronics", "MK 320,000", "MK 380,000",
        4.6f, 521, "2.3k sold", R.drawable.easylogo,
        description = "HP 15.6\" laptop with Intel Core i5, 8GB RAM, 512GB SSD. Ideal for students and professionals. Comes with Windows 11 pre-installed.",
        supplier = "HP Authorized Dealer", minOrder = "1 piece", deliveryTime = "3-5 days"
    ),
    Product(
        "3", "LED Study Lamp", "Furniture", "MK 12,000", "MK 16,000",
        4.3f, 189, "670 sold", R.drawable.easylogo,
        description = "Eye-care LED desk lamp with adjustable brightness and color temperature. USB charging port included. Foldable and portable design.",
        supplier = "Home Essentials MW", minOrder = "1 piece", deliveryTime = "1-3 days"
    ),
    Product(
        "4", "iPhone 13 128GB", "Electronics", "MK 850,000", "MK 950,000",
        4.9f, 1203, "4.5k sold", R.drawable.easylogo,
        description = "Apple iPhone 13 with A15 Bionic chip, 12MP dual camera system, 5G capable. Available in Midnight, Starlight, Blue, Pink, and Red.",
        supplier = "Apple Premium Reseller", minOrder = "1 piece", deliveryTime = "5-7 days"
    ),
    Product(
        "5", "Canvas Backpack", "Fashion", "MK 8,000", "MK 11,000",
        4.4f, 267, "1.1k sold", R.drawable.easylogo,
        description = "Durable canvas backpack with 30L capacity. Multiple compartments, padded laptop sleeve, and ergonomic shoulder straps.",
        supplier = "Urban Gear MW", minOrder = "1 piece", deliveryTime = "2-4 days"
    ),
    Product(
        "6", "Wireless Headphones", "Electronics", "MK 25,000", "MK 35,000",
        4.5f, 445, "1.8k sold", R.drawable.easylogo,
        description = "Bluetooth 5.0 over-ear headphones with active noise cancellation. 30-hour battery life, foldable design, built-in microphone.",
        supplier = "Audio World Malawi", minOrder = "1 piece", deliveryTime = "3-5 days"
    ),
    Product(
        "7", "Ergonomic Office Chair", "Furniture", "MK 75,000", "MK 95,000",
        4.2f, 98, "320 sold", R.drawable.easylogo,
        description = "Adjustable lumbar support chair with breathable mesh back, adjustable armrests, and 360° swivel base. Holds up to 120kg.",
        supplier = "Office Comfort MW", minOrder = "1 piece", deliveryTime = "5-10 days"
    ),
    Product(
        "8", "Premium Notebook A4", "Books", "MK 2,500", "MK 3,500",
        4.7f, 612, "3.2k sold", R.drawable.easylogo,
        description = "200-page A4 hardcover notebook with ruled pages. Lay-flat binding, bookmark ribbon, and elastic closure band.",
        supplier = "Stationery Hub MW", minOrder = "2 pieces", deliveryTime = "1-2 days"
    )
)

@Composable
fun HomeScreen(
    isGuest: Boolean,
    likedProducts: Set<String>,
    onProductClick: (Product) -> Unit,
    onToggleLike: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit,
    // FIX: This must call auth.signOut() on the anonymous session
    // before navigating — handled in AppNavigation's onNavigateToLogin lambda
    onNavigateToLogin: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showLoginDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Books", "Electronics", "Fashion", "Furniture")

    val filteredProducts = sampleProducts.filter {
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        val matchesSearch = it.name.contains(search, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // FIX: Pass the real onNavigateToLogin so the dialog routes correctly.
    // Previously the dialog's onLogin just called onNavigateToLogin() which
    // pushed Auth without signing out the anonymous session — Firebase would
    // then immediately bounce back to Main via the LaunchedEffect in AuthScreen.
    LoginRequiredDialog(
        show = showLoginDialog,
        onDismiss = { showLoginDialog = false },
        onLogin = {
            showLoginDialog = false
            onNavigateToLogin() // This now signs out anonymous first (see AppNavigation)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Orange, Color(0xFFFF8C42))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    "EasyCart",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    if (isGuest) "Browse as Guest · Sign in to order" else "Find great deals today",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(14.dp))

                // Search bar
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    placeholder = { Text("Search products…", color = GrayText) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Orange)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Orange,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }
        }

        // Categories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    text = category,
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        // Products label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Popular Products",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                "${filteredProducts.size} items",
                fontSize = 13.sp,
                color = GrayText
            )
        }

        // Product grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredProducts) { product ->
                HomeProductCard(
                    product = product,
                    isLiked = likedProducts.contains(product.id),
                    isGuest = isGuest,
                    onClick = { onProductClick(product) },
                    onToggleLike = {
                        if (isGuest) {
                            showLoginDialog = true
                        } else {
                            onToggleLike(product.id)
                        }
                    },
                    onAddToCart = {
                        // FIX: Show login dialog instead of silently failing.
                        // The dialog's onLogin calls onNavigateToLogin which now
                        // correctly signs out the anonymous session before routing.
                        if (isGuest) showLoginDialog = true
                        else onAddToCart(product)
                    }
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun HomeProductCard(
    product: Product,
    isLiked: Boolean,
    isGuest: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box {
            Column {
                // Product image
                Box {
                    Image(
                        painter = painterResource(product.image),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Discount badge
                    if (product.originalPrice.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Orange)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            val orig =
                                product.originalPrice.filter { it.isDigit() }.toLongOrNull() ?: 1L
                            val curr =
                                product.price.filter { it.isDigit() }.toLongOrNull() ?: 1L
                            val disc = ((orig - curr) * 100 / orig).toInt()
                            Text(
                                "-$disc%",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = DarkText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))

                    // Ratings row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { i ->
                            val filled = i < product.rating.toInt()
                            Text(
                                if (filled) "★" else "☆",
                                color = if (filled) Color(0xFFFFB300) else GrayText,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "(${product.reviewCount})",
                            fontSize = 10.sp,
                            color = GrayText
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        product.price,
                        color = Orange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    if (product.originalPrice.isNotEmpty()) {
                        Text(
                            product.originalPrice,
                            color = GrayText,
                            fontSize = 11.sp,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(
                        product.soldCount,
                        fontSize = 10.sp,
                        color = GrayText
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            if (isGuest) "Add (Login)" else "Add to Cart",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Heart button top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onToggleLike() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    tint = if (isLiked) Color(0xFFE53935) else GrayText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Orange else OrangeSoft
    val textColor = if (selected) Color.White else Orange

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun LoginRequiredDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onLogin: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Login Required", fontWeight = FontWeight.Bold) },
        text = {
            Text("You need to sign in before adding products to your cart or making a purchase.")
        },
        confirmButton = {
            Button(
                onClick = onLogin,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) { Text("Sign In") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Keep Browsing") }
        }
    )
}