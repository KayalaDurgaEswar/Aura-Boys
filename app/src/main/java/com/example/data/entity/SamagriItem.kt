package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "samagri")
data class SamagriItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val category: String = "Daily Puja", // Daily Puja, Sthapana, Visarjan, Maha Prasad
    val isArranged: Boolean = false,
    val assignedTo: String = ""
)
