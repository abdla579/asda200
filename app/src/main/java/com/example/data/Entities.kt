package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,   // العميل
    DRIVER,     // السائق / المندوب
    MERCHANT,   // التاجر
    ADMIN       // مدير النظام
}

enum class ServiceType {
    FOOD,       // توصيل الطعام
    PARCEL,     // توصيل الطرود والمستندات
    STORES,     // شراء المنتجات من المتاجر
    RIDES,      // حجز سيارات (أوبر / إن درايف)
    TRUCKS,     // شاحنات نقل البضائع والأثاث
    INTERCITY,  // الشحن والتنقل بين المحافظات
    HOME_SERVICES // خدمات منزلية مستقبلية
}

enum class OrderStatus {
    PENDING,    // قيد الانتظار
    ACCEPTED,   // تم قبول الطلب
    PREPARING,  // جاري التجهيز
    ON_THE_WAY, // المندوب في الطريق
    DELIVERED,  // تم التوصيل بنجاح
    CANCELLED   // ملغي
}

enum class PaymentMethod {
    CASH,           // الدفع النقدي
    BANK_CARD,      // بطاقة بنكية
    VODAFONE_CASH,  // فودافون كاش
    INSTAPAY        // إنستا باي
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "usr_default",
    val name: String = "أحمد محمود",
    val phone: String = "01012345678",
    val email: String = "ahmed@withegypt.eg",
    val currentRole: UserRole = UserRole.CUSTOMER,
    val isRtlLanguage: Boolean = true, // true = Arabic, false = English
    val isLoggedIn: Boolean = true,
    val avatarUrl: String = ""
)

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameAr: String,
    val nameEn: String,
    val category: String, // مطاعم, سوبرماركت, صيدليات, إلكترونيات
    val rating: Double = 4.8,
    val deliveryTimeMinutes: Int = 30,
    val deliveryFeeEgp: Double = 25.0,
    val addressAr: String,
    val isFeatured: Boolean = true,
    val isOpen: Boolean = true
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storeId: Long,
    val nameAr: String,
    val nameEn: String,
    val priceEgp: Double,
    val category: String,
    val descriptionAr: String,
    val inStock: Boolean = true
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serviceType: ServiceType,
    val customerName: String,
    val customerPhone: String,
    val pickupAddress: String,
    val dropoffAddress: String,
    val itemsSummary: String,
    val totalAmountEgp: Double,
    val discountEgp: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val status: OrderStatus = OrderStatus.PENDING,
    val driverName: String = "محمود الكابتن",
    val driverPhone: String = "01198765432",
    val driverRating: Double = 4.9,
    val estimatedMinutes: Int = 25,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val storeId: Long,
    val storeName: String,
    val productId: Long,
    val productName: String,
    val priceEgp: Double,
    val quantity: Int
)

@Entity(tableName = "promo_codes")
data class PromoCodeEntity(
    @PrimaryKey val code: String,
    val discountPercentage: Int,
    val maxDiscountEgp: Double,
    val isExpired: Boolean = false
)

@Entity(tableName = "driver_docs")
data class DriverDocumentEntity(
    @PrimaryKey val driverId: String = "drv_101",
    val nationalIdStatus: String = "تم التحقق",
    val licenseStatus: String = "تم التحقق",
    val vehicleModel: String = "نيسان صني 2024",
    val isVerified: Boolean = true,
    val isOnline: Boolean = true,
    val dailyEarningsEgp: Double = 850.0,
    val monthlyEarningsEgp: Double = 18400.0,
    val completedTrips: Int = 142
)
