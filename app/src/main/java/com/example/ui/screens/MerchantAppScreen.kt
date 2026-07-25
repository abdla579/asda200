package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptNavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantAppScreen(
    viewModel: AppViewModel,
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    isRtl: Boolean
) {
    var isStoreOpen by remember { mutableStateOf(true) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    var newProductName by remember { mutableStateOf("") }
    var newProductPrice by remember { mutableStateOf("") }
    var newProductCategory by remember { mutableStateOf("الوجبات الرئيسية") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Merchant Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EgyptNavyBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("متجر كشري أبو طارق", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                        Text("فرع وسط البلد - الموزع المعتمد", fontSize = 11.sp, color = Color.LightGray)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isStoreOpen) "المتجر مفتوح 🟢" else "مغلق 🔴", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isStoreOpen,
                            onCheckedChange = { isStoreOpen = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EgyptEmerald
                            ),
                            modifier = Modifier.testTag("merchant_status_switch")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sales Metrics Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("مبيعات اليوم 📈", fontSize = 11.sp, color = Color.Gray)
                    Text("4,250 ج.م", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EgyptEmerald)
                    Text("28 طلب تم شحنه", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("المخزون المتوفر 📦", fontSize = 11.sp, color = Color.Gray)
                    Text("${products.size} صنف فعال", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EgyptNavyBlue)
                    Text("جاهز لاستقبال الطلبات", fontSize = 10.sp, color = EgyptEmerald)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Incoming Store Orders Queue
        Text("الطلبات الواردة للمتجر (استقبال وتجهيز) 🛒", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
        Spacer(modifier = Modifier.height(8.dp))

        val merchantOrders = orders.filter { it.serviceType == ServiceType.STORES || it.serviceType == ServiceType.FOOD }
        if (merchantOrders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text("لا توجد طلبات جديدة في قائمة التجهيز حاليًا", modifier = Modifier.padding(20.dp), color = Color.Gray)
            }
        } else {
            merchantOrders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("#${order.id} - ${order.customerName}", fontWeight = FontWeight.Bold)
                            Text("${order.totalAmountEgp} ج.م", fontWeight = FontWeight.Bold, color = EgyptEmerald)
                        }
                        Text(order.itemsSummary, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.updateOrderStatus(order, OrderStatus.PREPARING) },
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("جاري التجهيز 👨‍🍳", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.updateOrderStatus(order, OrderStatus.ON_THE_WAY) },
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("جاهز للتسليم 🛵", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Product Catalog Management
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("قائمة المنتجات والأسعار 📦", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
            Button(
                onClick = { showAddProductDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_product_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة منتج", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        products.forEach { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(product.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(product.descriptionAr, fontSize = 11.sp, color = Color.Gray)
                        Text("${product.priceEgp} ج.م", fontWeight = FontWeight.ExtraBold, color = EgyptEmerald, fontSize = 13.sp)
                    }

                    Surface(color = EgyptEmerald.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                        Text("متوفر في المخزون 🟢", color = EgyptEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }

    // Modal Add Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val priceNum = newProductPrice.toDoubleOrNull() ?: 50.0
                        if (newProductName.isNotBlank()) {
                            viewModel.addNewProduct(1, newProductName, priceNum, newProductCategory)
                            showAddProductDialog = false
                            newProductName = ""
                            newProductPrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald)
                ) {
                    Text("حفظ وإضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("إلغاء")
                }
            },
            title = { Text("إضافة منتج جديد للمتجر", fontWeight = FontWeight.Bold, color = EgyptNavyBlue) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("اسم المنتج") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newProductPrice,
                        onValueChange = { newProductPrice = it },
                        label = { Text("السعر (بالجنيه المصري)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        )
    }
}
