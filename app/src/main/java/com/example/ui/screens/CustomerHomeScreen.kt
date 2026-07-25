package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.LiveMapVisualizer
import com.example.ui.theme.EgyptDarkBlue
import com.example.ui.theme.EgyptEmerald
import com.example.ui.theme.EgyptGold
import com.example.ui.theme.EgyptNavyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    viewModel: AppViewModel,
    stores: List<StoreEntity>,
    products: List<ProductEntity>,
    activeOrders: List<OrderEntity>,
    allOrders: List<OrderEntity>,
    isRtl: Boolean,
    onOpenCart: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf(ServiceType.FOOD) }
    var selectedStoreForMenu by remember { mutableStateOf<StoreEntity?>(null) }

    // Booking input states
    var pickupLoc by remember { mutableStateOf("القاهرة - وسط البلد") }
    var dropoffLoc by remember { mutableStateOf("الجيزة - شارع الأهرام") }
    var intercityFrom by remember { mutableStateOf("القاهرة") }
    var intercityTo by remember { mutableStateOf("الإسكندرية") }
    var rideVehicleType by remember { mutableStateOf("سيدان اقتصادي") }
    var parcelWeightKg by remember { mutableStateOf("1 - 5 كجم") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isRtl) "ابحث عن مطعم، أكل، كابتن، أو شحنة..." else "Search restaurant, ride, or parcel...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = EgyptNavyBlue) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .testTag("search_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = EgyptEmerald,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Banners Carousel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "With Egypt Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(EgyptDarkBlue.copy(alpha = 0.85f), Color.Transparent)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                ) {
                    Surface(color = EgyptGold, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "🔥 عروض راحتك تهمنا",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EgyptDarkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "خصم 20% بكود WITH2026",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "توصيل طعام، طرود، حجز سيارات وشحن محافظات",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Service Categories Title
        Text(
            text = if (isRtl) "أقسام الخدمات الذكية 🚀" else "Smart Services 🚀",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = EgyptNavyBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Grid (Horizontal Scroll & Selection)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val categories = listOf(
                ServiceCategoryItem(ServiceType.FOOD, "توصيل الطعام", "Food Delivery", Icons.Default.Restaurant, Color(0xFFEF4444)),
                ServiceCategoryItem(ServiceType.STORES, "المتاجر", "Stores", Icons.Default.Storefront, Color(0xFF3B82F6)),
                ServiceCategoryItem(ServiceType.PARCEL, "توصيل الطرود", "Parcels & Docs", Icons.Default.LocalPostOffice, Color(0xFF10B981)),
                ServiceCategoryItem(ServiceType.RIDES, "حجز سيارات", "Car Rides", Icons.Default.DirectionsCar, Color(0xFFF59E0B)),
                ServiceCategoryItem(ServiceType.TRUCKS, "شاحنات نقل", "Truck Rental", Icons.Default.LocalShipping, Color(0xFF8B5CF6)),
                ServiceCategoryItem(ServiceType.INTERCITY, "شحن المحافظات", "Intercity", Icons.Default.AltRoute, Color(0xFF06B6D4)),
                ServiceCategoryItem(ServiceType.HOME_SERVICES, "خدمات منزلية", "Home Services", Icons.Default.HomeRepairService, Color(0xFFEC4899))
            )

            items(categories) { category ->
                val isSelected = selectedService == category.type
                Card(
                    modifier = Modifier
                        .width(100.dp)
                        .clickable { selectedService = category.type }
                        .testTag("service_card_${category.type.name.lowercase()}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) EgyptNavyBlue else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EgyptEmerald else category.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.titleAr,
                                tint = if (isSelected) EgyptDarkBlue else category.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isRtl) category.titleAr else category.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynamic Active Service Section Card
        AnimatedContent(targetState = selectedService, label = "service_section") { service ->
            when (service) {
                ServiceType.FOOD, ServiceType.STORES -> {
                    // Stores & Restaurants list
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (service == ServiceType.FOOD) "أقرب المطاعم والوجبات 🍔" else "أقرب المتاجر والسوبرماركت 🛒",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EgyptNavyBlue
                            )
                            TextButton(onClick = { }) {
                                Text("عرض الكل", color = EgyptEmerald, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val filteredStores = stores.filter {
                            searchQuery.isEmpty() || it.nameAr.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredStores.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Text(
                                    text = "لا توجد نتائج مطابقة لبحثك",
                                    modifier = Modifier.padding(24.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            filteredStores.forEach { store ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { selectedStoreForMenu = store }
                                        .testTag("store_item_${store.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(EgyptNavyBlue.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (service == ServiceType.FOOD) Icons.Default.Restaurant else Icons.Default.ShoppingBag,
                                                contentDescription = null,
                                                tint = EgyptNavyBlue,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(store.nameAr, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(color = EgyptGold.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                                    Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Star, contentDescription = null, tint = EgyptGold, modifier = Modifier.size(12.dp))
                                                        Text("${store.rating}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EgyptDarkBlue)
                                                    }
                                                }
                                            }
                                            Text(store.addressAr, fontSize = 11.sp, color = Color.Gray)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row {
                                                Text("⏱️ ${store.deliveryTimeMinutes} دقيقة", fontSize = 11.sp, color = EgyptEmerald, fontWeight = FontWeight.SemiBold)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("🛵 التوصيل: ${store.deliveryFeeEgp} ج.م", fontSize = 11.sp, color = EgyptNavyBlue)
                                            }
                                        }

                                        Button(
                                            onClick = { selectedStoreForMenu = store },
                                            colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("المنيو 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ServiceType.PARCEL -> {
                    // Parcel Delivery Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📦 خدمة توصيل الطرود والمستندات السريعة", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EgyptNavyBlue)
                            Text("مندوب يربط بين نقطة الاستلام والتسليم فورًا", fontSize = 11.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = pickupLoc,
                                onValueChange = { pickupLoc = it },
                                label = { Text("عنوان الاستلام (من)") },
                                leadingIcon = { Icon(Icons.Default.TripOrigin, contentDescription = null, tint = EgyptEmerald) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = dropoffLoc,
                                onValueChange = { dropoffLoc = it },
                                label = { Text("عنوان التسليم (إلى)") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Red) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                OutlinedTextField(
                                    value = parcelWeightKg,
                                    onValueChange = { parcelWeightKg = it },
                                    label = { Text("وزن الطرد") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = EgyptNavyBlue.copy(alpha = 0.1f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("التكلفة المقدرة", fontSize = 10.sp, color = Color.Gray)
                                        Text("35.00 ج.م", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EgyptNavyBlue)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.createServiceBooking(
                                        type = ServiceType.PARCEL,
                                        pickup = pickupLoc,
                                        dropoff = dropoffLoc,
                                        price = 35.0,
                                        summary = "طرد مستندات ($parcelWeightKg)",
                                        paymentMethod = PaymentMethod.CASH
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("book_parcel_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("طلب مندوب طرود الآن 🚀", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                ServiceType.RIDES -> {
                    // Car Rides Form (Uber/InDrive style)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🚗 حجز سيارات ذكية (أوبر / إن درايف)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EgyptNavyBlue)
                            Text("اختر نقطة الانطلاق والوصول وفئة السيارة", fontSize = 11.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = pickupLoc,
                                onValueChange = { pickupLoc = it },
                                label = { Text("موقعك الحالي (انطلاق)") },
                                leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, tint = EgyptEmerald) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = dropoffLoc,
                                onValueChange = { dropoffLoc = it },
                                label = { Text("إلى أين تريد الذهاب؟") },
                                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Vehicle Category Selection
                            Text("فئة السيارة:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    Triple("اقتصادي", "65 ج.م", Icons.Default.DirectionsCar),
                                    Triple("راحة VIP", "95 ج.م", Icons.Default.DirectionsCarFilled),
                                    Triple("سيدات", "75 ج.م", Icons.Default.Person)
                                ).forEach { option ->
                                    val isSelected = rideVehicleType == option.first
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { rideVehicleType = option.first },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) EgyptNavyBlue else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(option.third, contentDescription = null, tint = if (isSelected) EgyptEmerald else Color.Gray, modifier = Modifier.size(20.dp))
                                            Text(option.first, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                                            Text(option.second, fontSize = 10.sp, color = if (isSelected) EgyptGold else EgyptEmerald)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val price = if (rideVehicleType.contains("VIP")) 95.0 else 65.0
                                    viewModel.createServiceBooking(
                                        type = ServiceType.RIDES,
                                        pickup = pickupLoc,
                                        dropoff = dropoffLoc,
                                        price = price,
                                        summary = "رحلة سيارة ($rideVehicleType)",
                                        paymentMethod = PaymentMethod.CASH
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("book_ride_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("تأكيد حجز السيارة 🚗", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                }

                ServiceType.TRUCKS -> {
                    // Truck Rental & Freight Moving
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🚚 حجز شاحنات نقل البضائع والأثاث", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EgyptNavyBlue)
                            Text("شاحنات جامبو، ربع نقل، ونقل ثقيل لكافة الأغراض", fontSize = 11.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = pickupLoc,
                                onValueChange = { pickupLoc = it },
                                label = { Text("موقع التحميل (من)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = dropoffLoc,
                                onValueChange = { dropoffLoc = it },
                                label = { Text("موقع التنزيل (إلى)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.createServiceBooking(
                                        type = ServiceType.TRUCKS,
                                        pickup = pickupLoc,
                                        dropoff = dropoffLoc,
                                        price = 350.0,
                                        summary = "شاحنة نقل بضائع ربع نقل",
                                        paymentMethod = PaymentMethod.CASH
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptEmerald),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("طلب شاحنة نقل (350 ج.م) 🚚", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                ServiceType.INTERCITY -> {
                    // Intercity Shipping between Egyptian Governorates
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🏙️ الشحن والتنقل بين المحافظات المصرية", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EgyptNavyBlue)
                            Text("توصيل سريع وسفر مريح بين جميع المحافظات", fontSize = 11.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = intercityFrom,
                                    onValueChange = { intercityFrom = it },
                                    label = { Text("من محافظة") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = intercityTo,
                                    onValueChange = { intercityTo = it },
                                    label = { Text("إلى محافظة") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.createServiceBooking(
                                        type = ServiceType.INTERCITY,
                                        pickup = intercityFrom,
                                        dropoff = intercityTo,
                                        price = 180.0,
                                        summary = "شحن سريع من $intercityFrom إلى $intercityTo",
                                        paymentMethod = PaymentMethod.CASH
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("حجز رحلة بين المحافظات (180 ج.م) 🏙️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                ServiceType.HOME_SERVICES -> {
                    // Home Services Future Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HomeRepairService, contentDescription = null, tint = EgyptEmerald, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("🛠️ الخدمات المنزلية الذكية (قريبًا)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EgyptNavyBlue)
                            Text("صيانة منزلية، سباكة، كهرباء، ونظافة باحترافية وتأمين شامـل.", textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Map Active Tracking Section (If active orders exist)
        if (activeOrders.isNotEmpty()) {
            val currentActive = activeOrders.first()
            Text(
                text = "تتبع طلبك الحالي 📍",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EgyptNavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            LiveMapVisualizer(
                pickupAddress = currentActive.pickupAddress,
                dropoffAddress = currentActive.dropoffAddress,
                driverName = currentActive.driverName,
                driverPhone = currentActive.driverPhone,
                statusText = AppViewModel.getStatusNameAr(currentActive.status),
                estimatedMinutes = currentActive.estimatedMinutes
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Recent Orders History List
        if (allOrders.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الطلبات السابقة والمفضلة 🕒",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EgyptNavyBlue
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            allOrders.take(3).forEach { order ->
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
                            Text("#${order.id} - ${AppViewModel.getServiceNameAr(order.serviceType)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(order.itemsSummary, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                            Text("${order.totalAmountEgp} ج.م", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EgyptEmerald)
                        }

                        Surface(
                            color = EgyptNavyBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = AppViewModel.getStatusNameAr(order.status),
                                color = EgyptNavyBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Restaurant / Store Menu Catalog
    if (selectedStoreForMenu != null) {
        val store = selectedStoreForMenu!!
        val storeProducts = products.filter { it.storeId == store.id }

        AlertDialog(
            onDismissRequest = { selectedStoreForMenu = null },
            confirmButton = {
                Button(
                    onClick = { selectedStoreForMenu = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EgyptNavyBlue)
                ) {
                    Text("إغلاق القائمة")
                }
            },
            title = {
                Text("منيو ${store.nameAr}", fontWeight = FontWeight.Bold, color = EgyptNavyBlue)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(store.addressAr, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (storeProducts.isEmpty()) {
                        Text("جاري تحميل منتجات المنيو...", color = Color.Gray)
                    } else {
                        storeProducts.forEach { product ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(product.descriptionAr, fontSize = 10.sp, color = Color.Gray)
                                    Text("${product.priceEgp} ج.م", fontWeight = FontWeight.Bold, color = EgyptEmerald, fontSize = 12.sp)
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.addToCart(product, store.nameAr)
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(EgyptEmerald)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "إضافة للسلة", tint = Color.White)
                                }
                            }
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        )
    }
}

private data class ServiceCategoryItem(
    val type: ServiceType,
    val titleAr: String,
    val titleEn: String,
    val icon: ImageVector,
    val color: Color
)
