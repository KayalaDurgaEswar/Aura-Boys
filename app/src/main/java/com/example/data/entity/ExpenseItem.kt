package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // Murti & Sthapana, Pandal & Lighting, Sound & Dhol, Prasad & Bhog, Puja Samagri, Visarjan Procession, Permissions, Miscellaneous
    val amount: Double,
    val vendor: String = "",
    val paidBy: String = "",
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val receiptRef: String = ""
)
