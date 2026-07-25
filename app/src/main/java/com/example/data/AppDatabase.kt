package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StoreEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        CartItemEntity::class,
        PromoCodeEntity::class,
        DriverDocumentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "with_egypt_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.appDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: AppDao) {
            // Default User
            dao.insertUser(UserEntity())

            // Default Stores
            val defaultStores = listOf(
                StoreEntity(id = 1, nameAr = "كشري أبو طارق - وسط البلد", nameEn = "Koshary Abou Tarek", category = "مطاعم", rating = 4.9, deliveryTimeMinutes = 25, deliveryFeeEgp = 20.0, addressAr = "وسط البلد، القاهرة"),
                StoreEntity(id = 2, nameAr = "سوبرماركت الفرجاني - التجمع الخامس", nameEn = "El Fergani Supermarket", category = "سوبرماركت", rating = 4.8, deliveryTimeMinutes = 35, deliveryFeeEgp = 30.0, addressAr = "شارع التسعين، التجمع الخامس"),
                StoreEntity(id = 3, nameAr = "صيدلية العزبي - المعادي", nameEn = "El Ezaby Pharmacy", category = "صيدليات", rating = 4.9, deliveryTimeMinutes = 20, deliveryFeeEgp = 15.0, addressAr = "شارع النصر، المعادي"),
                StoreEntity(id = 4, nameAr = "البيك - مدينة نصر", nameEn = "ALBAIK Cairo", category = "مطاعم", rating = 4.9, deliveryTimeMinutes = 30, deliveryFeeEgp = 25.0, addressAr = "طريق النصر، مدينة نصر"),
                StoreEntity(id = 5, nameAr = "مصر للإلكترونيات - الجيزة", nameEn = "Egypt Tech Store", category = "إلكترونيات", rating = 4.7, deliveryTimeMinutes = 45, deliveryFeeEgp = 40.0, addressAr = "شارع الأهرام، الجيزة")
            )
            dao.insertStores(defaultStores)

            // Default Products
            val defaultProducts = listOf(
                ProductEntity(storeId = 1, nameAr = "طبق كشري أبو طارق المخصوص", nameEn = "Abou Tarek Special Koshary", priceEgp = 45.0, category = "وجبات", descriptionAr = "أرز، مكرونة، عدس، حمص، بصل مقرمش مع الصلصة والتقلية الشهيرة"),
                ProductEntity(storeId = 1, nameAr = "طاجن لحمة بالبصل في الفرن", nameEn = "Beef Casserole", priceEgp = 75.0, category = "طواجن", descriptionAr = "قطع لحم بلدي طازج مع البصل والبهارات المصرية"),
                ProductEntity(storeId = 2, nameAr = "كرتونة حليب جهينة كامل الدسم 1L", nameEn = "Juhayna Milk 1L", priceEgp = 42.0, category = "ألبان", descriptionAr = "حليب بقر طازج مبستر 100%"),
                ProductEntity(storeId = 2, nameAr = "عبوة زيت ذرة كريستال 1.6L", nameEn = "Crystal Corn Oil 1.6L", priceEgp = 125.0, category = "زيوت", descriptionAr = "زيت ذرة نقّي صحي للمطبخ"),
                ProductEntity(storeId = 3, nameAr = "فيتامين C مع زنك - عبوة 30 قرص", nameEn = "Vitamin C + Zinc", priceEgp = 90.0, category = "مكملات", descriptionAr = "تقوية المناعة اليومية"),
                ProductEntity(storeId = 4, nameAr = "وجبة البيك 4 قطع دجاج حراق", nameEn = "ALBAIK 4 Pcs Spicy Chicken", priceEgp = 135.0, category = "وجبات دجاج", descriptionAr = "دجاج مقرمش حار مع الثوم والبطاطس والخبز"),
                ProductEntity(storeId = 5, nameAr = "باور بنك أنكر 20000 مللي أمبير", nameEn = "Anker PowerBank 20000mAh", priceEgp = 1200.0, category = "إلكترونيات", descriptionAr = "شحن سريع بقدرة 22.5 واط للموبايلات")
            )
            dao.insertProducts(defaultProducts)

            // Promos
            dao.insertPromoCode(PromoCodeEntity("WITH2026", 20, 100.0))
            dao.insertPromoCode(PromoCodeEntity("RAHATAK", 15, 50.0))
            dao.insertPromoCode(PromoCodeEntity("EGYPT", 25, 150.0))

            // Default Driver Docs
            dao.updateDriverDocs(DriverDocumentEntity())

            // Seed 1 active order for live map preview
            dao.insertOrder(
                OrderEntity(
                    serviceType = ServiceType.FOOD,
                    customerName = "أحمد محمود",
                    customerPhone = "01012345678",
                    pickupAddress = "كشري أبو طارق - وسط البلد",
                    dropoffAddress = "شارع كورنيش النيل - المعادي",
                    itemsSummary = "طبق كشري أبو طارق المخصوص (x2)",
                    totalAmountEgp = 110.0,
                    paymentMethod = PaymentMethod.VODAFONE_CASH,
                    status = OrderStatus.ON_THE_WAY,
                    driverName = "محمود الكابتن (تويوتا ياريس)",
                    driverPhone = "01198765432",
                    estimatedMinutes = 18
                )
            )
        }
    }
}
