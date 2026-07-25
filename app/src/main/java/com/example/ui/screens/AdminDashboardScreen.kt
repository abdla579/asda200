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
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@Composable
fun AdminDashboardScreen(
    viewModel: AppViewModel,
    orders: List<OrderEntity>,
    isRtl: Boolean
) {
    var notificationMessage by remember { mutableStateOf("") }
    var baseFareKm by remember { mutableStateOf("6.50") }
    var platformCommissionPercent by remember { mutableStateOf("15") }
    var promoCodeInput by remember { mutableStateOf("") }
    var promoDiscountInput by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Executive Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EgyptDarkBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("لوحة تحكم منصة With Egypt 👑", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                Text("إدارة الخدمات، السائقين، التجار، العمولات وإشعارات النظام", fontSize = 11.sp, color = EgyptGold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform KPI Metrics Grid
        Text("إحصائيات وتقارير المنصة المباشرة 📊", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("إجمالي العملاء 👥", "12,450", "نشط هذا الشهر", EgyptNavyBlue, Modifier.weight(1f))
                KpiCard("السائقين المسجلين 🚗", "1,820", "1,240 متصل الآن", EgyptEmerald, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KpiCard("التجار والمتاجر 🏪", "480", "في 14 محافظة", EgyptNavyBlue, Modifier.weight(1f))
                KpiCard("أرباح المنصة 💵", "128,500 ج.م", "عمولة $platformCommissionPercent%", EgyptGold, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Broadcast System FCM Notification
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📢 بث إشعار فوري لجميع المستخدمين (Firebase FCM)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EgyptNavyBlue)
                Text("أرسل تنبيهًا فوريًا لجميع التطبيقات (العميل، السائق، التاجر)", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notificationMessage,
                    onValueChange = { notificationMessage = it },
                    placeholder = { Text("اكتب نص الإشعار هنا... (مثال: خصم 20% بمناسبة افتتاح فرع الإسكندرية)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (notificationMessage.isNotBlank()) {
                            viewModel.broadcastAdminNotification(notificationMessage)
                            notificationMessage = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("broadcast_notif_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إرسال الإشعار الفوري الآن 🚀", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pricing & Fare Configuration
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚙️ إدارة أسعار الكيلومتر وعمولة المنصة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EgyptNavyBlue)

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = baseFareKm,
                        onValueChange = { baseFareKm = it },
                        label = { Text("سعر الكيلو (ج.م)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = platformCommissionPercent,
                        onValueChange = { platformCommissionPercent = it },
                        label = { Text("عمولة التطبيق (%)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.showNotification("تم تحديث جدول الأسعار والعمولات بنجاح ✅") },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حفظ التعديلات", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Driver Verification Approval Queue
        Text("طلبات انضمام السائقين الجديدة 📄", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            Triple("السائق: أحمد حسن علي", "رخصة قيادة ثانية - نيسان صني", "بانتظار المراجعة"),
            Triple("السائق: مينا إبراهيم", "رخصة قيادة أسطول - تويوتا هايس", "مكتملة")
        ).forEach { driver ->
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
                        Text(driver.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(driver.second, fontSize = 11.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { viewModel.showNotification("تم اعتماد السائق وتفعيل حسابه 🟢") },
                        colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("اعتماد الحساب", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, sub: String, accentColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = accentColor)
            Text(sub, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
