package com.example.easycart.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val Orange = Color(0xFFFF6B00)
private val OrangeSoft = Color(0xFFFFF3EB)
private val DarkText = Color(0xFF1A1A2E)
private val GrayText = Color(0xFF9E9E9E)
private val GreenSuccess = Color(0xFF2E7D32)
private val GreenSoft = Color(0xFFE8F5E9)

// ── Payment method enum ────────────────────────────────────────────────────────
enum class PaymentMethod(val label: String, val subtitle: String, val icon: ImageVector) {
    AIRTEL_MONEY("Airtel Money", "Pay via Airtel Money", Icons.Filled.PhoneAndroid),
    TNM_MPAMBA("TNM Mpamba", "Pay via TNM Mpamba", Icons.Filled.PhoneAndroid),
    BANK_CARD("Bank Card", "Visa / Mastercard", Icons.Filled.CreditCard),
    CASH_ON_DELIVERY("Cash on Delivery", "Pay when your order arrives", Icons.Filled.LocalShipping)
}

// ── PaymentScreen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    // Delivery address fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    // Payment
    var selectedMethod by remember { mutableStateOf<PaymentMethod?>(null) }
    var mobileNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    // UI state
    var showConfirmation by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    // Totals
    val subtotal = cartItems.sumOf { item ->
        (item.product.price.filter { it.isDigit() }.toLongOrNull() ?: 0L) * item.quantity
    }
    val deliveryFee = if (subtotal > 100_000L) 0L else 2_500L
    val total = subtotal + deliveryFee

    fun formatMK(amount: Long) = "MK ${"%,d".format(amount)}"

    fun validate(): Boolean {
        return when {
            fullName.isBlank() -> { formError = "Please enter your full name"; false }
            phone.isBlank() -> { formError = "Please enter your phone number"; false }
            area.isBlank() -> { formError = "Please enter your area / street"; false }
            city.isBlank() -> { formError = "Please enter your city"; false }
            selectedMethod == null -> { formError = "Please select a payment method"; false }
            selectedMethod == PaymentMethod.AIRTEL_MONEY && mobileNumber.isBlank() ->
                { formError = "Please enter your Airtel Money number"; false }
            selectedMethod == PaymentMethod.TNM_MPAMBA && mobileNumber.isBlank() ->
                { formError = "Please enter your TNM Mpamba number"; false }
            selectedMethod == PaymentMethod.BANK_CARD && (cardNumber.isBlank() || cardExpiry.isBlank() || cardCvv.isBlank() || cardName.isBlank()) ->
                { formError = "Please complete all card details"; false }
            else -> { formError = null; true }
        }
    }

    // ── Order Confirmation Dialog ──────────────────────────────────────────────
    if (showConfirmation) {
        OrderConfirmationDialog(
            orderNumber = "EC-${(100000..999999).random()}",
            total = formatMK(total),
            paymentMethod = selectedMethod?.label ?: "",
            onDone = onOrderPlaced
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Orange, Color(0xFFFF8C42))))
                .padding(horizontal = 8.dp, vertical = 14.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "Checkout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Step indicator ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepDot(number = 1, label = "Address", active = true, done = false)
            StepLine()
            StepDot(number = 2, label = "Payment", active = true, done = false)
            StepLine()
            StepDot(number = 3, label = "Confirm", active = false, done = false)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Delivery address ─────────────────────────────────────────────
            item {
                SectionCard(title = "Delivery Address", icon = Icons.Default.LocationOn) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PaymentTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            leadingIcon = Icons.Default.Person
                        )
                        PaymentTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Phone Number",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone
                        )
                        PaymentTextField(
                            value = area,
                            onValueChange = { area = it },
                            label = "Area / Street",
                            leadingIcon = Icons.Default.Home
                        )
                        PaymentTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City / Town",
                            leadingIcon = Icons.Default.LocationCity
                        )
                    }
                }
            }

            // ── Payment method ────────────────────────────────────────────────
            item {
                SectionCard(title = "Payment Method", icon = Icons.Default.Payment) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PaymentMethod.entries.forEach { method ->
                            PaymentMethodOption(
                                method = method,
                                selected = selectedMethod == method,
                                onClick = {
                                    selectedMethod = method
                                    mobileNumber = ""
                                    cardNumber = ""; cardExpiry = ""; cardCvv = ""; cardName = ""
                                }
                            )

                            // Inline input for selected method
                            AnimatedVisibility(
                                visible = selectedMethod == method,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OrangeSoft)
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    when (method) {
                                        PaymentMethod.AIRTEL_MONEY,
                                        PaymentMethod.TNM_MPAMBA -> {
                                            Text(
                                                "Enter your ${method.label} number",
                                                fontSize = 12.sp,
                                                color = Orange,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            PaymentTextField(
                                                value = mobileNumber,
                                                onValueChange = { mobileNumber = it },
                                                label = "Mobile Money Number",
                                                leadingIcon = Icons.Default.PhoneAndroid,
                                                keyboardType = KeyboardType.Phone
                                            )
                                            Text(
                                                "You will receive a prompt on your phone to confirm payment.",
                                                fontSize = 12.sp,
                                                color = GrayText
                                            )
                                        }
                                        PaymentMethod.BANK_CARD -> {
                                            Text(
                                                "Enter your card details",
                                                fontSize = 12.sp,
                                                color = Orange,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            PaymentTextField(
                                                value = cardName,
                                                onValueChange = { cardName = it },
                                                label = "Name on Card",
                                                leadingIcon = Icons.Default.Person
                                            )
                                            PaymentTextField(
                                                value = cardNumber,
                                                onValueChange = {
                                                    if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() }
                                                },
                                                label = "Card Number",
                                                leadingIcon = Icons.Default.CreditCard,
                                                keyboardType = KeyboardType.Number,
                                                placeholder = "•••• •••• •••• ••••"
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                PaymentTextField(
                                                    value = cardExpiry,
                                                    onValueChange = {
                                                        val digits = it.filter { c -> c.isDigit() }.take(4)
                                                        cardExpiry = if (digits.length >= 3)
                                                            digits.substring(0, 2) + "/" + digits.substring(2)
                                                        else digits
                                                    },
                                                    label = "MM/YY",
                                                    keyboardType = KeyboardType.Number,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                PaymentTextField(
                                                    value = cardCvv,
                                                    onValueChange = {
                                                        if (it.length <= 3) cardCvv = it.filter { c -> c.isDigit() }
                                                    },
                                                    label = "CVV",
                                                    keyboardType = KeyboardType.Number,
                                                    placeholder = "•••",
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                        PaymentMethod.CASH_ON_DELIVERY -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = GreenSuccess,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    "Pay in cash when your order arrives at your door.",
                                                    fontSize = 13.sp,
                                                    color = DarkText
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Order summary ─────────────────────────────────────────────────
            item {
                SectionCard(title = "Order Summary", icon = Icons.Default.Receipt) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.product.name,
                                    fontSize = 13.sp,
                                    color = DarkText,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "×${item.quantity}",
                                    fontSize = 12.sp,
                                    color = GrayText
                                )
                                Spacer(Modifier.width(8.dp))
                                val lineTotal = (item.product.price.filter { it.isDigit() }.toLongOrNull() ?: 0L) * item.quantity
                                Text(
                                    formatMK(lineTotal),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkText
                                )
                            }
                        }

                        Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 4.dp))

                        SummaryRow("Subtotal", formatMK(subtotal))
                        SummaryRow(
                            label = "Delivery",
                            value = if (deliveryFee == 0L) "FREE" else formatMK(deliveryFee),
                            valueColor = if (deliveryFee == 0L) GreenSuccess else DarkText
                        )
                        if (deliveryFee == 0L) {
                            Text(
                                "🎉 Free delivery on orders above MK 100,000",
                                fontSize = 11.sp,
                                color = GreenSuccess
                            )
                        }

                        Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = DarkText)
                            Text(formatMK(total), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Orange)
                        }
                    }
                }
            }

            // ── Error message ─────────────────────────────────────────────────
            item {
                AnimatedVisibility(visible = formError != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                        Text(formError ?: "", fontSize = 13.sp, color = Color(0xFFE53935))
                    }
                }
            }
        }

        // ── Place order button ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = {
                    if (validate()) {
                        isProcessing = true
                        // Simulate processing delay then show confirmation
                        showConfirmation = true
                        isProcessing = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Place Order · ${formatMK(total)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// ── Order Confirmation Dialog ─────────────────────────────────────────────────
@Composable
fun OrderConfirmationDialog(
    orderNumber: String,
    total: String,
    paymentMethod: String,
    onDone: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GreenSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GreenSuccess,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Order Placed!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "Your order has been received and is being processed.",
                    fontSize = 14.sp,
                    color = GrayText,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(20.dp))

                // Order details box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F8F8))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ConfirmRow("Order Number", orderNumber, valueColor = Orange)
                    ConfirmRow("Amount Paid", total, valueColor = GreenSuccess)
                    ConfirmRow("Payment", paymentMethod)
                    ConfirmRow("Status", "Confirmed ✓", valueColor = GreenSuccess)
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Estimated delivery: 3–7 business days",
                    fontSize = 13.sp,
                    color = GrayText
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Continue Shopping", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrangeSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = "",
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = if (placeholder.isNotEmpty()) {{ Text(placeholder, color = GrayText) }} else null,
        leadingIcon = leadingIcon?.let { icon -> { Icon(icon, contentDescription = null, tint = Orange, modifier = Modifier.size(18.dp)) } },
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Orange,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedLabelColor = Orange
        )
    )
}

@Composable
fun PaymentMethodOption(
    method: PaymentMethod,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Orange else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (selected) OrangeSoft else Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (selected) Orange else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                method.icon,
                contentDescription = null,
                tint = if (selected) Color.White else GrayText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(method.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = DarkText)
            Text(method.subtitle, fontSize = 12.sp, color = GrayText)
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (selected) Orange else Color(0xFFBDBDBD), CircleShape)
                .background(if (selected) Orange else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
            }
        }
    }
}

@Composable
fun StepDot(number: Int, label: String, active: Boolean, done: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (active || done) Orange else Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text("$number", color = if (active) Color.White else GrayText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = if (active || done) Orange else GrayText, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
fun RowScope.StepLine() {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .background(Color(0xFFE0E0E0))
    )
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = DarkText) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = GrayText)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
fun ConfirmRow(label: String, value: String, valueColor: Color = DarkText) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = GrayText)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
