package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())
    }

    val userState: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserEntity())

    val storesState: StateFlow<List<StoreEntity>> = repository.allStores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrdersState: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOrdersState: StateFlow<List<OrderEntity>> = repository.activeOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemsState: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val driverDocsState: StateFlow<DriverDocumentEntity?> = repository.driverDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DriverDocumentEntity())

    val allProductsState: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state
    val selectedService = MutableStateFlow(ServiceType.FOOD)
    val searchQuery = MutableStateFlow("")
    val promoCodeInput = MutableStateFlow("")
    val appliedDiscountEgp = MutableStateFlow(0.0)
    val promoMessage = MutableStateFlow<String?>(null)
    val notificationSnack = MutableStateFlow<String?>(null)

    // Role state
    fun switchRole(newRole: UserRole) {
        viewModelScope.launch {
            val current = userState.value ?: UserEntity()
            repository.saveUser(current.copy(currentRole = newRole))
            showNotification("تم الانتقال إلى دور: ${getRoleNameAr(newRole)}")
        }
    }

    // Language switch
    fun toggleLanguage() {
        viewModelScope.launch {
            val current = userState.value ?: UserEntity()
            val newIsRtl = !current.isRtlLanguage
            repository.saveUser(current.copy(isRtlLanguage = newIsRtl))
            val langName = if (newIsRtl) "العربية (RTL)" else "English (LTR)"
            showNotification("تم تغيير اللغة إلى $langName")
        }
    }

    // Cart Operations
    fun addToCart(product: ProductEntity, storeName: String) {
        viewModelScope.launch {
            val existing = cartItemsState.value.find { it.productId == product.id }
            if (existing != null) {
                repository.addToCart(existing.copy(quantity = existing.quantity + 1))
            } else {
                repository.addToCart(
                    CartItemEntity(
                        storeId = product.storeId,
                        storeName = storeName,
                        productId = product.id,
                        productName = product.nameAr,
                        priceEgp = product.priceEgp,
                        quantity = 1
                    )
                )
            }
            showNotification("تمت إضافة ${product.nameAr} إلى السلة")
        }
    }

    fun removeFromCart(itemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(itemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun applyPromoCode() {
        val code = promoCodeInput.value.trim().uppercase()
        if (code.isEmpty()) return
        viewModelScope.launch {
            val promo = repository.checkPromoCode(code)
            if (promo != null && !promo.isExpired) {
                val discount = promo.maxDiscountEgp
                appliedDiscountEgp.value = discount
                promoMessage.value = "تم تطبيق الكود $code بنجاح! خصم $discount جنيه"
                showNotification("كود خصم مفعل: -$discount ج.م")
            } else {
                promoMessage.value = "كود الخصم غير صحيح أو منتهي الصلاحية"
            }
        }
    }

    // Checkout Order Placement
    fun placeCartOrder(paymentMethod: PaymentMethod, pickupAddr: String, dropoffAddr: String) {
        viewModelScope.launch {
            val items = cartItemsState.value
            if (items.isEmpty()) return@launch

            val subtotal = items.sumOf { it.priceEgp * it.quantity }
            val total = (subtotal + 25.0 - appliedDiscountEgp.value).coerceAtLeast(0.0)
            val itemsDesc = items.joinToString(", ") { "${it.productName} (x${it.quantity})" }

            val newOrder = OrderEntity(
                serviceType = ServiceType.STORES,
                customerName = userState.value?.name ?: "أحمد محمود",
                customerPhone = userState.value?.phone ?: "01012345678",
                pickupAddress = pickupAddr,
                dropoffAddress = dropoffAddr,
                itemsSummary = itemsDesc,
                totalAmountEgp = total,
                discountEgp = appliedDiscountEgp.value,
                paymentMethod = paymentMethod,
                status = OrderStatus.PENDING,
                driverName = "جاري البحث عن كابتن قريب...",
                estimatedMinutes = 25
            )

            val newId = repository.createOrder(newOrder)
            clearCart()
            appliedDiscountEgp.value = 0.0
            promoCodeInput.value = ""
            showNotification("تم إرسال الطلب #$newId بنجاح! جاري التوصيل...")
        }
    }

    // Quick Book Ride or Truck or Intercity Service
    fun createServiceBooking(
        type: ServiceType,
        pickup: String,
        dropoff: String,
        price: Double,
        summary: String,
        paymentMethod: PaymentMethod
    ) {
        viewModelScope.launch {
            val newOrder = OrderEntity(
                serviceType = type,
                customerName = userState.value?.name ?: "أحمد محمود",
                customerPhone = userState.value?.phone ?: "01012345678",
                pickupAddress = pickup,
                dropoffAddress = dropoff,
                itemsSummary = summary,
                totalAmountEgp = price,
                paymentMethod = paymentMethod,
                status = OrderStatus.ACCEPTED,
                driverName = "كابتن أحمد علي (متاح الآن)",
                driverPhone = "01099887766",
                estimatedMinutes = 15
            )
            val orderId = repository.createOrder(newOrder)
            showNotification("تم حجز ${getServiceNameAr(type)} بنجاح! رقم الحجز #$orderId")
        }
    }

    // Driver actions
    fun updateOrderStatus(order: OrderEntity, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = newStatus))
            showNotification("تم تحديث حالة الطلب #${order.id} إلى: ${getStatusNameAr(newStatus)}")
        }
    }

    fun toggleDriverOnline() {
        viewModelScope.launch {
            val current = driverDocsState.value ?: DriverDocumentEntity()
            val updated = current.copy(isOnline = !current.isOnline)
            repository.updateDriverDocs(updated)
            showNotification(if (updated.isOnline) "أنت الآن متصل ومتاح لاستقبال الطلبات 🟢" else "أنت الآن غير متصل 🔴")
        }
    }

    // Merchant Actions
    fun addNewProduct(storeId: Long, nameAr: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    storeId = storeId,
                    nameAr = nameAr,
                    nameEn = nameAr,
                    priceEgp = price,
                    category = category,
                    descriptionAr = "منتج طازج جديد مضاف من التاجر"
                )
            )
            showNotification("تمت إضافة المنتج $nameAr إلى القائمة بنجاح!")
        }
    }

    // Admin Broadcast Notification
    fun broadcastAdminNotification(message: String) {
        showNotification("📢 إشعار عام من الإدارة: $message")
    }

    fun showNotification(msg: String) {
        notificationSnack.value = msg
    }

    fun clearNotification() {
        notificationSnack.value = null
    }

    companion object {
        fun getRoleNameAr(role: UserRole): String = when(role) {
            UserRole.CUSTOMER -> "العميل"
            UserRole.DRIVER -> "الكابتن / السائق"
            UserRole.MERCHANT -> "التاجر"
            UserRole.ADMIN -> "لوحة الإدارة"
        }

        fun getServiceNameAr(type: ServiceType): String = when(type) {
            ServiceType.FOOD -> "توصيل الطعام"
            ServiceType.PARCEL -> "توصيل الطرود والمستندات"
            ServiceType.STORES -> "شراء المتاجر"
            ServiceType.RIDES -> "حجز سيارات (أوبر)"
            ServiceType.TRUCKS -> "شاحنات النقل"
            ServiceType.INTERCITY -> "الشحن بين المحافظات"
            ServiceType.HOME_SERVICES -> "خدمات منزلية"
        }

        fun getStatusNameAr(status: OrderStatus): String = when(status) {
            OrderStatus.PENDING -> "قيد الانتظار"
            OrderStatus.ACCEPTED -> "تم القبول"
            OrderStatus.PREPARING -> "جاري التجهيز"
            OrderStatus.ON_THE_WAY -> "في الطريق إليك"
            OrderStatus.DELIVERED -> "تم التوصيل"
            OrderStatus.CANCELLED -> "ملغي"
        }
    }
}
