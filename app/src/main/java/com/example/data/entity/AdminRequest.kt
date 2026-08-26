package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_requests")
data class AdminRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val requesterName: String,
    val phone: String = "",
    val reason: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val requestedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)
