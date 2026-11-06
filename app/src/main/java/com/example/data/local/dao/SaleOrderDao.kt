package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SaleOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleOrderDao {
    @Query("SELECT * FROM sale_orders ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleOrder>>

    @Query("SELECT * FROM sale_orders ORDER BY timestamp DESC")
    suspend fun getAllSalesSync(): List<SaleOrder>

    @Query("SELECT * FROM sale_orders ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSales(limit: Int = 5): Flow<List<SaleOrder>>

    @Query("SELECT * FROM sale_orders WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getSalesBetween(startTime: Long, endTime: Long): Flow<List<SaleOrder>>

    @Query("SELECT * FROM sale_orders WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getSalesForCustomer(customerId: Long): Flow<List<SaleOrder>>

    @Query("SELECT * FROM sale_orders WHERE customerId = :customerId AND (isCredit = 1 OR dueAmount > 0) ORDER BY timestamp DESC")
    fun getCreditSalesForCustomer(customerId: Long): Flow<List<SaleOrder>>

    @Query("SELECT * FROM sale_orders WHERE customerId = :customerId AND (isCredit = 1 OR dueAmount > 0) ORDER BY timestamp DESC")
    suspend fun getCreditSalesForCustomerSync(customerId: Long): List<SaleOrder>

    @Query("SELECT * FROM sale_orders WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: Long): SaleOrder?

    @Query("SELECT SUM(dueAmount) FROM sale_orders WHERE dueAmount > 0")
    fun getTotalUnpaidBaki(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sale_orders")
    suspend fun getSalesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(order: SaleOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<SaleOrder>)

    @Update
    suspend fun updateSale(order: SaleOrder)

    @Delete
    suspend fun deleteSale(order: SaleOrder)

    @Query("DELETE FROM sale_orders WHERE id = :id")
    suspend fun deleteSaleById(id: Long)

    @Query("DELETE FROM sale_orders")
    suspend fun clearAll()
}
