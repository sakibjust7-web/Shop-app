package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class PurchaseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitBuyingPrice: Double,
    val totalCost: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val supplierName: String = "",
    val note: String = "",
    val supplierId: Long? = null,
    val paidAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val billPhotoUri: String? = null
)
