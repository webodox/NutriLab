package com.example.nutrilab.data.entity

data class UserProfileEntity(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
)