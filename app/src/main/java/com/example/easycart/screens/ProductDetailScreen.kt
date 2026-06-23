package com.example.easycart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycart.R

private val Orange = Color(0xFFFF6B00)
private val OrangeSoft = Color(0xFFFFF3EB)
private val DarkText = Color(0xFF1A1A2E)
private val GrayText = Color(0xFF9E9E9E)
private val GrayBg = Color(0xFFF8F8F8)
private val GreenAccent = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isGuest: Boolean,
    isLiked: Boolean,
    onBack: () -> Unit,
    onToggleLike: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var selectedImageIndex by remember { mutableStateOf(0) }
    var quantity by remember { mutableStateOf(1) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val allImages = if (product.images.isNotEmpty()) product.images else listOf(product.image, product.image, product.image)

    LoginRequiredDialog(
        show = showLoginDialog,
        onDismiss = { showLoginDialog = false },
        onLogin = {
            showLoginDialog = false
            onNavigateToLogin()
        }
    )

    Box(modifier = Modifier.fillMaxSize().background(GrayBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Main image
            item {
                Box {
                    Image(
                        painter = painterResource(allImages[selectedImageIndex]),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay at bottom for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                                )
                            )
                    )

                    // Back button
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(38.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { onBack() }
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkText, modifier = Modifier.size(20.dp))
                    }

                    // Like button
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(38.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                if (isGuest) showLoginDialog = true
                                else onToggleLike()
                            }
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isLiked) Color(0xFFE53935) else GrayText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Image count badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${selectedImageIndex + 1}/${allImages.size}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Thumbnail strip
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allImages.indices.toList()) { idx ->
                        val selected = idx == selectedImageIndex
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) Orange else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageIndex = idx }
                        ) {
                            Image(
                                painter = painterResource(allImages[idx]),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Price & title section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Price row
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            product.price,
                            color = Orange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        )
                        if (product.originalPrice.isNotEmpty()) {
                            Spacer(Modifier.width(10.dp))
                            Text(
                                product.originalPrice,
                                color = GrayText,
                                fontSize = 14.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(Modifier.width(8.dp))
                            val orig = product.originalPrice.filter { it.isDigit() }.toLongOrNull() ?: 1L
                            val curr = product.price.filter { it.isDigit() }.toLongOrNull() ?: 1L
                            val disc = ((orig - curr) * 100 / orig).toInt()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Orange)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("-$disc%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DarkText,
                        lineHeight = 26.sp
                    )

                    Spacer(Modifier.height(10.dp))

                    // Stats row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("${product.rating}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkText)
                        }
                        Text("|", color = Color(0xFFE0E0E0))
                        Text("${product.reviewCount} reviews", fontSize = 13.sp, color = GrayText)
                        Text("|", color = Color(0xFFE0E0E0))
                        Text(product.soldCount, fontSize = 13.sp, color = GrayText)
                    }
                }
            }

            // Quantity selector
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Quantity", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = DarkText)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(OrangeSoft)
                                .clickable { if (quantity > 1) quantity-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("−", fontSize = 20.sp, color = Orange, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("$quantity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Spacer(Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Orange)
                                .clickable { quantity++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Supplier & shipping info
            item {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Supplier", fontSize = 11.sp, color = GrayText)
                            Text(product.supplier, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        }
                    }
                    Divider(color = Color(0xFFF0F0F0))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Delivery", fontSize = 11.sp, color = GrayText)
                            Text("Ships in ${product.deliveryTime}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        }
                    }
                    Divider(color = Color(0xFFF0F0F0))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Buyer Protection", fontSize = 11.sp, color = GrayText)
                            Text("Money-back guarantee if item not received", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        }
                    }
                }
            }

            // Product detail tabs (Description / Specs / Reviews)
            item {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Orange
                    ) {
                        listOf("Details", "Specs", "Reviews").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                            )
                        }
                    }

                    Box(modifier = Modifier.padding(16.dp)) {
                        when (selectedTab) {
                            0 -> {
                                // Description
                                Text(
                                    product.description.ifEmpty {
                                        "High quality ${product.name} from ${product.supplier}. Category: ${product.category}. Minimum order: ${product.minOrder}."
                                    },
                                    fontSize = 14.sp,
                                    color = DarkText,
                                    lineHeight = 22.sp
                                )
                            }
                            1 -> {
                                // Specs
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SpecRow("Category", product.category)
                                    SpecRow("Min. Order", product.minOrder)
                                    SpecRow("Delivery Time", product.deliveryTime)
                                    SpecRow("Supplier", product.supplier)
                                    SpecRow("Current Price", product.price)
                                    if (product.originalPrice.isNotEmpty()) {
                                        SpecRow("Original Price", product.originalPrice)
                                    }
                                }
                            }
                            2 -> {
                                // Reviews
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    // Overall rating
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${product.rating}", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Orange)
                                            Row {
                                                repeat(5) { i ->
                                                    Icon(
                                                        Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (i < product.rating.toInt()) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Text("${product.reviewCount} reviews", fontSize = 11.sp, color = GrayText)
                                        }
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            listOf(5 to 0.72f, 4 to 0.15f, 3 to 0.08f, 2 to 0.03f, 1 to 0.02f).forEach { (star, pct) ->
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("$star", fontSize = 11.sp, color = GrayText, modifier = Modifier.width(12.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    LinearProgressIndicator(
                                                        progress = pct,
                                                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                        color = Orange,
                                                        trackColor = Color(0xFFF0F0F0)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Divider(color = Color(0xFFF0F0F0))

                                    // Sample reviews
                                    listOf(
                                        Triple("Chisomo M.", 5, "Excellent quality! Fast delivery too. Would definitely order again."),
                                        Triple("Tapiwa N.", 4, "Good product, matches the description. Packaging was neat."),
                                        Triple("Grace K.", 5, "Very happy with my purchase! The quality exceeded expectations.")
                                    ).forEach { (name, stars, comment) ->
                                        ReviewItem(name, stars, comment)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom action bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(8.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        if (isGuest) showLoginDialog = true
                        else onAddToCart(product)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Orange)
                    )
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (isGuest) "Cart (Login)" else "Add to Cart",
                        color = Orange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        if (isGuest) showLoginDialog = true
                        else onBuyNow(product)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text(
                        if (isGuest) "Buy (Login)" else "Buy Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = GrayText, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = DarkText, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2f))
    }
    Divider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun ReviewItem(name: String, stars: Int, comment: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Orange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(name.first().toString(), color = Orange, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkText)
            }
            Row {
                repeat(5) { i ->
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < stars) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(comment, fontSize = 13.sp, color = DarkText, lineHeight = 20.sp)
        Spacer(Modifier.height(12.dp))
        Divider(color = Color(0xFFF0F0F0))
    }
}
