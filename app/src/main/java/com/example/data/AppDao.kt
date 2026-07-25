package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Queries
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String = "usr_default"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String = "usr_default"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Stores & Restaurants Queries
    @Query("SELECT * FROM stores ORDER BY rating DESC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreEntity>)

    @Update
    suspend fun updateStore(store: StoreEntity)

    // Products Queries
    @Query("SELECT * FROM products WHERE storeId = :storeId")
    fun getProductsForStore(storeId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // Orders Queries
    @Query("SELECT * FROM orders ORDER BY createdAtTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status != 'DELIVERED' AND status != 'CANCELLED' ORDER BY createdAtTimestamp DESC")
    fun getActiveOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    // Cart Queries
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun removeCartItem(itemId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Promo Codes
    @Query("SELECT * FROM promo_codes WHERE code = :code LIMIT 1")
    suspend fun getPromoCode(code: String): PromoCodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromoCode(promo: PromoCodeEntity)

    // Driver Documents & Earnings
    @Query("SELECT * FROM driver_docs WHERE driverId = :driverId LIMIT 1")
    fun getDriverDocs(driverId: String = "drv_101"): Flow<DriverDocumentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDriverDocs(docs: DriverDocumentEntity)
}
