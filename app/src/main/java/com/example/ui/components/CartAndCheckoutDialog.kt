package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItemEntity
import com.example.data.PaymentMethod
import com.example.ui.AppViewModel
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartAndCheckoutDialog(
    viewModel: AppViewModel,
    cartItems: List<CartItemEntity>,
    appliedDiscount: Double,
    promoMessage: String?,
    isRtl: Boolean,
    onDismiss: () -> Unit
) {
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var pickupAddr by remember { mutableStateOf("كشري أبو طارق - وسط البلد") }
    var dropoffAddr by remember { mutableStateOf("المعادي - شارع كورنيش النيل") }
    var promoInput by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.priceEgp * it.quantity }
    val deliveryFee = if (cartItems.isNotEmpty()) 25.0 else 0.0
    val total = (subtotal + deliveryFee - appliedDiscount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.placeCartOrder(paymentMethod, pickupAddr, dropoffAddr)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("confirm_order_btn")
                ) {
                    Text("تأكيد الطلب ($total ج.م) 🚀", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = EgyptNavyBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("سلة الشراء والدفع", fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
            ) {
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("السلة فارغة حاليًا 🛒", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartItems) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${item.priceEgp} ج.م × ${item.quantity}", fontSize = 11.sp, color = EgyptEmerald)
                                }

                                IconButton(onClick = { viewModel.removeFromCart(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Promo Code Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.promoCodeInput.collectAsState().value,
                            onValueChange = { viewModel.promoCodeInput.value = it },
                            placeholder = { Text("كود الخصم (WITH2026)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { viewModel.applyPromoCode() },
                            colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("تطبيق", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (promoMessage != null) {
                        Text(promoMessage, fontSize = 10.sp, color = EgyptEmerald, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payment Method Options
                    Text("طريقة الدفع:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EgyptNavyBlue)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Pair(PaymentMethod.CASH, "نقدي 💵"),
                            Pair(PaymentMethod.VODAFONE_CASH, "فودافون كاش 📱"),
                            Pair(PaymentMethod.INSTAPAY, "InstaPay 🏦"),
                            Pair(PaymentMethod.BANK_CARD, "بطاقة 💳")
                        ).forEach { method ->
                            val isSelected = paymentMethod == method.first
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { paymentMethod = method.first },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EgyptNavyBlue else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = method.second,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Summary Calculation Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الفرعي:", fontSize = 11.sp)
                                Text("$subtotal ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("رسوم التوصيل:", fontSize = 11.sp)
                                Text("$deliveryFee ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (appliedDiscount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("خصم الكوبون:", fontSize = 11.sp, color = EgyptEmerald)
                                    Text("-$appliedDiscount ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EgyptEmerald)
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الإجمالي النهائي:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("$total ج.م", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = EgyptNavyBlue)
                            }
                        }
                    }
                }
            }
        }
    )
}
