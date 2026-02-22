package com.example.nutrilab.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateJoined: Long = System.currentTimeMillis(),
    val role: String = "user"
)
