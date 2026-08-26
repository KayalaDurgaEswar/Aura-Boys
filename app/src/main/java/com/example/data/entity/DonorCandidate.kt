package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class DonorCandidate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val houseOrFlat: String = "",
    val phone: String = "",
    val targetAmount: Double,
    val paidAmount: Double = 0.0,
    val isPaid: Boolean = false,
    val paymentMode: String = "Cash", // Cash, UPI, Bank Transfer, Cheque
    val paymentDate: Long? = null,
    val transactionRef: String = "",
    val notes: String = "",
    val isApprovedByAdmin: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
