package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PurchaseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY timestamp DESC")
    fun getAllPurchases(): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchases ORDER BY timestamp DESC")
    suspend fun getAllPurchasesSync(): List<PurchaseRecord>

    @Query("SELECT * FROM purchases WHERE productId = :productId ORDER BY timestamp DESC")
    fun getPurchasesForProduct(productId: Long): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchases WHERE supplierId = :supplierId ORDER BY timestamp DESC")
    fun getPurchasesForSupplier(supplierId: Long): Flow<List<PurchaseRecord>>

    @Query("SELECT * FROM purchases WHERE supplierId = :supplierId OR (supplierName = :supplierName AND :supplierName != '') ORDER BY timestamp DESC")
    fun getPurchasesForSupplierOrName(supplierId: Long, supplierName: String): Flow<List<PurchaseRecord>>

    @Query("SELECT COUNT(*) FROM purchases")
    suspend fun getPurchaseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(purchases: List<PurchaseRecord>)

    @Query("DELETE FROM purchases")
    suspend fun clearAll()
}
