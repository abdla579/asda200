package com.example.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRole
import com.example.ui.AppViewModel
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptNavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    viewModel: AppViewModel,
    isRtl: Boolean,
    onDismiss: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("01012345678") }
    var otpInput by remember { mutableStateOf("") }
    var adminCodeInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var isOtpSent by remember { mutableStateOf(false) }

    val isAdminCodeEntered = adminCodeInput.trim() == "122006" ||
            phoneInput.trim() == "122006" ||
            otpInput.trim() == "122006"

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (!isOtpSent && !isAdminCodeEntered) {
                Button(
                    onClick = {
                        if (phoneInput.isNotBlank()) {
                            isOtpSent = true
                            viewModel.showNotification("تم إرسال رمز OTP كـ SMS إلى $phoneInput")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("إرسال كود OTP 📩", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (isAdminCodeEntered) {
                            viewModel.switchRole(UserRole.ADMIN)
                            viewModel.showNotification("تم تسجيل الدخول في وضع الإدارة 👑 بكود المرور 122006")
                        } else {
                            viewModel.switchRole(selectedRole)
                            val roleTitle = when (selectedRole) {
                                UserRole.CUSTOMER -> "عميل"
                                UserRole.MERCHANT -> "محل / تاجر"
                                UserRole.DRIVER -> "كابتن"
                                UserRole.ADMIN -> "مدير النظام"
                            }
                            viewModel.showNotification("تم تسجيل الدخول بنجاح كـ $roleTitle ✅")
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAdminCodeEntered) EgyptEmerald else EgyptNavyBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("auth_confirm_btn")
                ) {
                    Text(
                        text = if (isAdminCodeEntered) "دخول لوحة الإدارة 👑" else "تأكيد الدخول ✅",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAdminCodeEntered) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isAdminCodeEntered) EgyptEmerald else EgyptNavyBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAdminCodeEntered) "دخول مسؤول النظام (122006)" else "تسجيل الدخول في With Egypt",
                    fontWeight = FontWeight.Bold,
                    color = EgyptNavyBlue
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(scrollState)
            ) {
                // Role Selection Choices (Customer, Store/Merchant, Captain/Driver)
                Text("اختر نوع الحساب للتسجيل:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple(UserRole.CUSTOMER, "عميل 👤", "تسوق وتوصيل"),
                        Triple(UserRole.MERCHANT, "محل / تاجر 🏪", "إدارة المتجر"),
                        Triple(UserRole.DRIVER, "كابتن 🛵", "توصيل رحلات")
                    ).forEach { (role, label, sub) ->
                        val isSelected = selectedRole == role && !isAdminCodeEntered
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedRole = role }
                                .testTag("select_role_${role.name.lowercase()}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EgyptNavyBlue else Color(0xFFF1F5F9),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, EgyptEmerald) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.Black
                                )
                                Text(
                                    text = sub,
                                    fontSize = 9.sp,
                                    color = if (isSelected) EgyptEmerald else Color.Gray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Phone
                if (!isOtpSent) {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("رقم الموبايل (مثال: 01012345678)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EgyptEmerald) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("أدخل رمز التأكيد OTP (4 أرقام)") },
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = EgyptEmerald) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_otp_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secret Admin Code Input (122006)
                OutlinedTextField(
                    value = adminCodeInput,
                    onValueChange = { adminCodeInput = it },
                    label = { Text("كود الإدارة الخاص (اختياري - أدخل 122006)") },
                    placeholder = { Text("122006") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = if (isAdminCodeEntered) EgyptEmerald else Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_admin_code_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (isAdminCodeEntered) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        color = EgyptEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EgyptEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "تم التعرف على كود الإدارة (122006) 👑 سيتم تسجيل دخولك كـ مسؤول النظام",
                                color = EgyptDarkBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("أو الدخول السريع بواسطة:", fontSize = 11.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                // Social Sign-in buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            if (isAdminCodeEntered) {
                                viewModel.switchRole(UserRole.ADMIN)
                                viewModel.showNotification("تم الدخول بحساب Google لوضعيّة الإدارة 👑")
                            } else {
                                viewModel.switchRole(selectedRole)
                                viewModel.showNotification("تم تسجيل الدخول بواسطة Google 🟢")
                            }
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("متابعة باستخدام Google 🌐", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            if (isAdminCodeEntered) {
                                viewModel.switchRole(UserRole.ADMIN)
                                viewModel.showNotification("تم الدخول بحساب Apple لوضعيّة الإدارة 👑")
                            } else {
                                viewModel.switchRole(selectedRole)
                                viewModel.showNotification("تم تسجيل الدخول بواسطة Apple ID 🍎")
                            }
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("متابعة باستخدام Apple ID 🍎", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    )
}
