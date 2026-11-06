package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = "Shop Maintenance", // Rent, Electricity, Tea/Snacks, Staff, Transport, Repair, Other
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
