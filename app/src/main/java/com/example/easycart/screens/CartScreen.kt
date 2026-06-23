package com.example.easycart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Orange = Color(0xFFFF6B00)
private val OrangeSoft = Color(0xFFFFF3EB)
private val DarkText = Color(0xFF1A1A2E)
private val GrayText = Color(0xFF9E9E9E)

data class CartItem(
    val product: Product,
    val quantity: Int
)

@Composable
fun CartScreen(
    isGuest: Boolean,
    cartItems: List<CartItem>,
    onNavigateToLogin: () -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onUpdateQty: (String, Int) -> Unit,
    onCheckout: () -> Unit
) {
    if (isGuest) {
        GuestCartPlaceholder(onNavigateToLogin = onNavigateToLogin)
        return
    }

    if (cartItems.isEmpty()) {
        EmptyCartPlaceholder()
        return
    }

    // Calculate total
    val totalAmount = cartItems.sumOf { item ->
        val price = item.product.price
            .filter { it.isDigit() }
            .toLongOrNull() ?: 0L
        price * item.quantity
    }
    val formattedTotal = "MK ${"%,d".format(totalAmount)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Text(
                "My Cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )
            Text(
                "${cartItems.size} items",
                fontSize = 13.sp,
                color = GrayText,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartItems, key = { it.product.id }) { item ->
                CartItemCard(
                    item = item,
                    onRemove = { onRemoveFromCart(item.product.id) },
                    onDecrease = {
                        if (item.quantity > 1) onUpdateQty(item.product.id, item.quantity - 1)
                        else onRemoveFromCart(item.product.id)
                    },
                    onIncrease = {
                        onUpdateQty(item.product.id, item.quantity + 1)
                    }
                )
            }
        }

        // Order summary & checkout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${cartItems.size} item(s)", color = GrayText, fontSize = 13.sp)
                Text(formattedTotal, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DarkText)
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery fee", color = GrayText, fontSize = 13.sp)
                Text("Calculated at checkout", color = GrayText, fontSize = 12.sp)
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", color = GrayText, fontSize = 12.sp)
                    Text(formattedTotal, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Orange)
                }
                Button(
                    onClick = onCheckout,
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onRemove: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val itemTotal = (item.product.price.filter { it.isDigit() }.toLongOrNull() ?: 0L) * item.quantity

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Image(
                painter = painterResource(item.product.image),
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DarkText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(item.product.category, fontSize = 12.sp, color = GrayText)
                Spacer(Modifier.height(6.dp))
                Text(
                    item.product.price,
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Qty controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(OrangeSoft)
                                .clickable { onDecrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("−", fontSize = 16.sp, color = Orange, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("${item.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Spacer(Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Orange)
                                .clickable { onIncrease() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Item total
                    Text(
                        "MK ${"%,d".format(itemTotal)}",
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Delete button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun GuestCartPlaceholder(onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(OrangeSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text("Sign in to view your cart", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
        Spacer(Modifier.height(8.dp))
        Text("You're browsing as a guest.", fontSize = 14.sp, color = GrayText)
        Text("Create an account or sign in to add items.", fontSize = 14.sp, color = GrayText)
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onNavigateToLogin,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            modifier = Modifier.height(50.dp).width(200.dp)
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun EmptyCartPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(OrangeSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Orange, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Your cart is empty", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
        Spacer(Modifier.height(8.dp))
        Text("Browse products and add items to get started.", fontSize = 14.sp, color = GrayText)
    }
}
