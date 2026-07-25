package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    val currentUser: Flow<UserEntity?> = dao.getUserFlow()
    val allStores: Flow<List<StoreEntity>> = dao.getAllStores()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val activeOrders: Flow<List<OrderEntity>> = dao.getActiveOrders()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val driverDocs: Flow<DriverDocumentEntity?> = dao.getDriverDocs()

    fun getProductsForStore(storeId: Long): Flow<List<ProductEntity>> = dao.getProductsForStore(storeId)
    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAllProducts()
    fun getOrderById(orderId: Long): Flow<OrderEntity?> = dao.getOrderById(orderId)

    suspend fun saveUser(user: UserEntity) = dao.insertUser(user)
    suspend fun createOrder(order: OrderEntity): Long = dao.insertOrder(order)
    suspend fun updateOrder(order: OrderEntity) = dao.updateOrder(order)

    suspend fun addToCart(item: CartItemEntity) = dao.insertCartItem(item)
    suspend fun removeFromCart(itemId: Long) = dao.removeCartItem(itemId)
    suspend fun clearCart() = dao.clearCart()

    suspend fun checkPromoCode(code: String): PromoCodeEntity? = dao.getPromoCode(code)

    suspend fun updateDriverDocs(docs: DriverDocumentEntity) = dao.updateDriverDocs(docs)
    suspend fun addProduct(product: ProductEntity) = dao.insertProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = dao.deleteProduct(product)
    suspend fun updateStore(store: StoreEntity) = dao.updateStore(store)
}
