package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.LiveMapVisualizer
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@Composable
fun DriverAppScreen(
    viewModel: AppViewModel,
    driverDocs: DriverDocumentEntity?,
    orders: List<OrderEntity>,
    isRtl: Boolean
) {
    val docs = driverDocs ?: DriverDocumentEntity()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Driver Status Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EgyptDarkBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EgyptNavyBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("محمود الكابتن", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text(docs.vehicleModel, fontSize = 11.sp, color = Color.LightGray)
                        }
                    }

                    // Online Toggle Button
                    Button(
                        onClick = { viewModel.toggleDriverOnline() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (docs.isOnline) EgyptEmerald else Color.Red
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("driver_online_toggle")
                    ) {
                        Text(
                            text = if (docs.isOnline) "متصل 🟢" else "غير متصل 🔴",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Earnings Cards Row (Daily & Monthly)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("أرباح اليوم 💰", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${docs.dailyEarningsEgp} ج.م", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = EgyptEmerald)
                    Text("${docs.completedTrips} رحلة مكتملة", fontSize = 10.sp, color = EgyptNavyBlue)
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("أرباح الشهر 📅", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${docs.monthlyEarningsEgp} ج.م", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = EgyptNavyBlue)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = EgyptGold, modifier = Modifier.size(12.dp))
                        Text(" 4.9 تقييم العملاء", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active Orders & Incoming Requests
        Text("طلبات التوصيل المتاحة للقبول 🚗", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)

        Spacer(modifier = Modifier.height(10.dp))

        if (orders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text("لا توجد طلبات جديدة في منطقتك حاليًا", modifier = Modifier.padding(24.dp), color = Color.Gray)
            }
        } else {
            orders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = EgyptNavyBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    AppViewModel.getServiceNameAr(order.serviceType),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EgyptNavyBlue
                                )
                            }
                            Text("${order.totalAmountEgp} ج.م", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = EgyptEmerald)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("العميل: ${order.customerName} (${order.customerPhone})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("الاستلام من: ${order.pickupAddress}", fontSize = 11.sp, color = Color.Gray)
                        Text("التسليم إلى: ${order.dropoffAddress}", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (order.status == OrderStatus.PENDING || order.status == OrderStatus.ACCEPTED) {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order, OrderStatus.ON_THE_WAY) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("قبول وبدء الرحلة 🚀", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.updateOrderStatus(order, OrderStatus.CANCELLED) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("رفض الطلب ❌", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            } else if (order.status == OrderStatus.ON_THE_WAY) {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order, OrderStatus.DELIVERED) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("تأكيد التسليم وتلقي المبلـغ ✅", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation GPS Map Preview
        Text("خرائط الملاحة الحية (GPS Navigation)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
        Spacer(modifier = Modifier.height(8.dp))
        LiveMapVisualizer(
            pickupAddress = "نقطة استلام الشحنة - القااهرة",
            dropoffAddress = "نقطة التسليم للعميل - الجيزة",
            statusText = "الملاحة تعمل بدقة عالية",
            estimatedMinutes = 12
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Document Verification Status
        Text("التحقق من هويتك ومستندات السائق 📄", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                listOf(
                    Pair("بطاقة الرقم القومي المصرية", docs.nationalIdStatus),
                    Pair("رخصة القيادة الخاصة/المهنية", docs.licenseStatus),
                    Pair("فحص السيارة الفني (الفيش وتراخيص المرور)", "مكتمل")
                ).forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EgyptEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.first, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Surface(color = EgyptEmerald.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                            Text(item.second, color = EgyptEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Divider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}
