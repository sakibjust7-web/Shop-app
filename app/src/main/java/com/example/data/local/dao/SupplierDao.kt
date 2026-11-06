package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.Supplier
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY amountOwed DESC, name ASC")
    fun getAllSuppliersOwedFirst(): Flow<List<Supplier>>

    @Query("SELECT * FROM suppliers ORDER BY amountOwed DESC, name ASC")
    suspend fun getAllSuppliersSync(): List<Supplier>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    fun getSupplierById(id: Long): Flow<Supplier?>

    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getSupplierByIdSync(id: Long): Supplier?

    @Query("SELECT COUNT(*) FROM suppliers")
    suspend fun getSupplierCount(): Int

    @Query("SELECT SUM(amountOwed) FROM suppliers")
    fun getTotalAmountOwed(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(suppliers: List<Supplier>)

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    @Query("UPDATE suppliers SET amountOwed = amountOwed + :delta WHERE id = :supplierId")
    suspend fun adjustAmountOwed(supplierId: Long, delta: Double)

    @Query("UPDATE suppliers SET amountOwed = :newBalance WHERE id = :supplierId")
    suspend fun updateSupplierBalance(supplierId: Long, newBalance: Double)

    @Query("DELETE FROM suppliers")
    suspend fun clearAll()
}
