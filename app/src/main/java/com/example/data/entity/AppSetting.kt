package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class AppSetting(
    @PrimaryKey
    val key: String,
    val value: String
)
