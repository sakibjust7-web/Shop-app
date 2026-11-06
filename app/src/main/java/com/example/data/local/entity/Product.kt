package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val category: String,
    val description: String = "",
    val photoKey: String = "", // e.g., "hammer", "pvc_pipe", "paint", "lock", "switch", "nails", etc.
    val buyPrice: Double,
    val sellPrice: Double,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val unitBn: String = "টি",
    val unitEn: String = "pcs",
    val isDeleted: Boolean = false // Soft delete flag
) {
    fun localizedName(isBangla: Boolean): String = if (isBangla) nameBn else nameEn
    fun localizedUnit(isBangla: Boolean): String = if (isBangla) unitBn else unitEn
    val isLowStock: Boolean get() = stockQuantity <= lowStockThreshold
}
