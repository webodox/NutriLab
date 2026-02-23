package com.example.nutrilab.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val goalId: Int = 0,

    val goalName: String,
    val goalType: String,
    val createdAt: Long = System.currentTimeMillis()
)