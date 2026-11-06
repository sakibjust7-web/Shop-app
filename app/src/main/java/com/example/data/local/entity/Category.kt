package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameEn: String,
    val nameBn: String,
    val emoji: String,
    val sortOrder: Int,
    val isVisible: Boolean = true,
    val colorHex: String = "#10B981"
) {
    fun localizedName(isBangla: Boolean): String = if (isBangla) nameBn else nameEn
}
