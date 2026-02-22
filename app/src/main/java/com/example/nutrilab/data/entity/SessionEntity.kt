package com.example.nutrilab.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0,

    val userId: Int,
    val token: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)