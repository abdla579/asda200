package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@Composable
fun LiveMapVisualizer(
    pickupAddress: String = "كشري أبو طارق - وسط البلد",
    dropoffAddress: String = "كورنيش النيل - المعادي",
    driverName: String = "كابتن محمود (تويوتا ياريس)",
    driverPhone: String = "01198765432",
    statusText: String = "الكابتن في الطريق إليك (4.2 كم)",
    estimatedMinutes: Int = 18,
    modifier: Modifier = Modifier
) {
    // Pulse animation for driver pin
    val infiniteTransition = rememberInfiniteTransition(label = "map_anim")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driver_progress"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("live_map_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(EgyptEmerald)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التتبع المباشر للخريطة (Google Maps GPS)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EgyptNavyBlue
                    )
                }

                Surface(
                    color = EgyptEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⏱️ متبقي $estimatedMinutes دقيقة",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = EgyptEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Map Vector View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE2E8F0))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw map road background lines
                    val roadColor = Color(0xFFFFFFFF)
                    val mainRoadColor = Color(0xFF38BDF8)

                    // Secondary roads
                    drawLine(Color(0xFFCBD5E1), Offset(0f, height * 0.3f), Offset(width, height * 0.3f), strokeWidth = 14f)
                    drawLine(Color(0xFFCBD5E1), Offset(width * 0.4f, 0f), Offset(width * 0.4f, height), strokeWidth = 14f)
                    drawLine(Color(0xFFCBD5E1), Offset(0f, height * 0.7f), Offset(width, height * 0.7f), strokeWidth = 12f)

                    // Main route path (Nile Corniche Highway path)
                    val routePath = Path().apply {
                        moveTo(width * 0.15f, height * 0.25f)
                        cubicTo(
                            width * 0.35f, height * 0.15f,
                            width * 0.5f, height * 0.85f,
                            width * 0.85f, height * 0.75f
                        )
                    }

                    // Draw route glow path
                    drawPath(
                        path = routePath,
                        color = Color(0xFF93C5FD),
                        style = Stroke(width = 16f)
                    )

                    // Draw route dashed active line
                    drawPath(
                        path = routePath,
                        color = Color(0xFF0284C7),
                        style = Stroke(
                            width = 8f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    )

                    // Pickup Pin Point A (Green)
                    val pickupX = width * 0.15f
                    val pickupY = height * 0.25f
                    drawCircle(color = Color(0xFF10B981), radius = 18f, center = Offset(pickupX, pickupY))
                    drawCircle(color = Color.White, radius = 8f, center = Offset(pickupX, pickupY))

                    // Dropoff Pin Point B (Red)
                    val dropoffX = width * 0.85f
                    val dropoffY = height * 0.75f
                    drawCircle(color = Color(0xFFEF4444), radius = 18f, center = Offset(dropoffX, dropoffY))
                    drawCircle(color = Color.White, radius = 8f, center = Offset(dropoffX, dropoffY))

                    // Animated Driver Car Pin Position along path curve
                    val currentDriverX = pickupX + (dropoffX - pickupX) * progress
                    val currentDriverY = pickupY + (dropoffY - pickupY) * progress + (kotlin.math.sin(progress * 3.14) * 40f).toFloat()

                    drawCircle(color = Color(0xFF1E3A8A), radius = 24f, center = Offset(currentDriverX, currentDriverY))
                    drawCircle(color = Color(0xFF10B981), radius = 14f, center = Offset(currentDriverX, currentDriverY))
                    drawCircle(color = Color.White, radius = 6f, center = Offset(currentDriverX, currentDriverY))
                }

                // Overlay Map Badges
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    color = EgyptDarkBlue.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = EgyptGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مباشر GPS", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pickup / Dropoff details
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TripOrigin, contentDescription = "من", tint = EgyptEmerald, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("نقطة الانطلاق (من)", fontSize = 11.sp, color = Color.Gray)
                    Text(pickupAddress, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "إلى", tint = Color.Red, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("وجهة الوصول (إلى)", fontSize = 11.sp, color = Color.Gray)
                    Text(dropoffAddress, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Driver Contact Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = EgyptNavyBlue, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(driverName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(statusText, fontSize = 11.sp, color = EgyptEmerald, fontWeight = FontWeight.Medium)
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { /* Simulated Call */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EgyptEmerald)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
