package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserRole
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderAndTopBar(
    currentRole: UserRole,
    isRtl: Boolean,
    cartItemCount: Int,
    onRoleSelected: (UserRole) -> Unit,
    onToggleLanguage: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenLogin: () -> Unit
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = EgyptDarkBlue,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Top Row: Logo, Slogan, Lang toggle & Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRoleSelected(UserRole.CUSTOMER) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "With Egypt Logo",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "With Egypt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EgyptEmerald,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "راحتك تهمنا",
                                    color = EgyptDarkBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isRtl) "مستقبل الخدمات الذكية في مصر" else "Future of Smart Services in Egypt",
                            style = MaterialTheme.typography.labelSmall,
                            color = EgyptGold,
                            fontSize = 10.sp
                        )
                    }
                }

                // Actions: Language Toggle & Cart Badge & Account
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier.testTag("lang_toggle_btn")
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = if (isRtl) "EN" else "عربي",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Cart Badge
                    Box {
                        IconButton(
                            onClick = onOpenCart,
                            modifier = Modifier.testTag("cart_icon_btn")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "السلة", tint = Color.White)
                        }
                        if (cartItemCount > 0) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp),
                                containerColor = EgyptEmerald,
                                contentColor = Color.White
                            ) {
                                Text("$cartItemCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Account Login
                    IconButton(onClick = onOpenLogin) {
                        Icon(Icons.Default.Person, contentDescription = "تسجيل الدخول", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Role Switcher Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRtl) "وضع المستخدم:" else "Mode:",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )

                // Role selector chips (Public roles: Customer, Driver, Merchant)
                val publicRoles = listOf(UserRole.CUSTOMER, UserRole.DRIVER, UserRole.MERCHANT)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentRole == UserRole.ADMIN) {
                        Surface(
                            modifier = Modifier
                                .clickable { onRoleSelected(UserRole.ADMIN) }
                                .testTag("role_chip_admin"),
                            shape = RoundedCornerShape(12.dp),
                            color = EgyptEmerald
                        ) {
                            Text(
                                text = if (isRtl) "الإدارة 👑" else "Admin 👑",
                                color = EgyptDarkBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    publicRoles.forEach { role ->
                        val isSelected = currentRole == role
                        Surface(
                            modifier = Modifier
                                .clickable { onRoleSelected(role) }
                                .testTag("role_chip_${role.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EgyptEmerald else Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = when(role) {
                                    UserRole.CUSTOMER -> if (isRtl) "العميل" else "Customer"
                                    UserRole.DRIVER -> if (isRtl) "الكابتن" else "Captain"
                                    UserRole.MERCHANT -> if (isRtl) "المحل / التاجر" else "Store / Merchant"
                                    UserRole.ADMIN -> if (isRtl) "الإدارة" else "Admin"
                                },
                                color = if (isSelected) EgyptDarkBlue else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
