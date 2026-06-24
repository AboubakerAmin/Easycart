package com.example.easycart.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Orange = Color(0xFFFF6B00)
private val OrangeSoft = Color(0xFFFFF3EB)
private val DarkText = Color(0xFF1A1A2E)
private val GrayText = Color(0xFF9E9E9E)

@Composable
fun ProfileScreen(
    username: String,
    isGuest: Boolean,
    onNavigateToLogin: () -> Unit,
    onLogout: () -> Unit
) {
    // FIX: Pass onLogout to GuestProfilePlaceholder so guest users
    // can sign out of their anonymous session
    if (isGuest) {
        GuestProfilePlaceholder(
            onNavigateToLogin = onNavigateToLogin,
            onLogout = onLogout
        )
        return
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out of your account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Sign Out") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Profile header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Orange, Color(0xFFFF8C42)))
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("EasyCart Member", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Stats row
        item {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox("0", "Orders")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFF0F0F0))
                StatBox("0", "Wishlist")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFF0F0F0))
                StatBox("0", "Reviews")
            }
        }

        // Order sections
        item {
            Spacer(Modifier.height(12.dp))
            SectionHeader("My Orders")
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)) {
                val orderStatuses = listOf(
                    Triple(Icons.Outlined.Pending, "Pending", "Awaiting confirmation"),
                    Triple(Icons.Outlined.LocalShipping, "In Transit", "On the way"),
                    Triple(Icons.Outlined.CheckCircle, "Delivered", "Completed orders"),
                    Triple(Icons.Outlined.Cancel, "Cancelled", "Cancelled orders")
                )
                orderStatuses.forEach { (icon, label, sub) ->
                    ProfileMenuItem(icon = icon, label = label, subtitle = sub) {}
                    if (label != "Cancelled") Divider(
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }
        }

        // Account settings
        item {
            Spacer(Modifier.height(12.dp))
            SectionHeader("Account")
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)) {
                val settingsItems = listOf(
                    Triple(Icons.Outlined.Person, "Edit Profile", "Update your information"),
                    Triple(Icons.Outlined.LocationOn, "Delivery Addresses", "Manage your addresses"),
                    Triple(Icons.Outlined.Notifications, "Notifications", "Push & email alerts"),
                    Triple(Icons.Outlined.Lock, "Security", "Password & 2FA")
                )
                settingsItems.forEach { (icon, label, sub) ->
                    ProfileMenuItem(icon = icon, label = label, subtitle = sub) {}
                    if (label != "Security") Divider(
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }
        }

        // Support
        item {
            Spacer(Modifier.height(12.dp))
            SectionHeader("Support")
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)) {
                ProfileMenuItem(Icons.Outlined.HelpOutline, "Help Center", "FAQs and guides") {}
                Divider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(start = 56.dp))
                ProfileMenuItem(Icons.Outlined.Info, "About EasyCart", "Version 1.0.0") {}
            }
        }

        // Logout
        item {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { showLogoutDialog = true }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sign Out",
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Orange)
        Spacer(Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = GrayText)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = GrayText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OrangeSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Orange, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(subtitle, fontSize = 12.sp, color = GrayText)
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GuestProfilePlaceholder(
    onNavigateToLogin: () -> Unit,
    // FIX: Added onLogout parameter so guest users can sign out
    // of their anonymous Firebase session
    onLogout: () -> Unit
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Confirmation dialog before signing out anonymous session
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("This will end your guest session. Any unsaved data will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Sign Out") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSignOutDialog = false }) { Text("Cancel") }
            }
        )
    }

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
                Icons.Default.Person,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "You're browsing as a guest",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = DarkText
        )
        Spacer(Modifier.height(8.dp))
        Text("Sign in to access your profile,", fontSize = 14.sp, color = GrayText)
        Text("orders, wishlist and more.", fontSize = 14.sp, color = GrayText)
        Spacer(Modifier.height(28.dp))

        // Sign In / Register button
        Button(
            onClick = onNavigateToLogin,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            modifier = Modifier
                .height(50.dp)
                .width(220.dp)
        ) {
            Text("Sign In / Register", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(Modifier.height(16.dp))

        // FIX: Sign Out button for guest — ends anonymous session
        // so user is taken back to Onboarding/Auth screen
        TextButton(onClick = { showSignOutDialog = true }) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Sign Out",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}