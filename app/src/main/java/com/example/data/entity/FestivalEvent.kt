package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class FestivalEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayNumber: Int,
    val title: String,
    val dateStr: String,
    val timeStr: String,
    val location: String = "Main Pandal",
    val description: String = "",
    val isCompleted: Boolean = false
)
