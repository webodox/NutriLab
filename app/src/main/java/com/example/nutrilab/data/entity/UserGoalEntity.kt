package com.example.nutrilab.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_goals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["goalId"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("goalId")
    ]
)
data class UserGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val userGoalId: Int = 0,

    val userId: Int,
    val goalId: Int,

    val targetVal: Double? = null,
    val currProgress: Double? = null,

    val startDate: Long? = null,
    val targetDate: Long? = null,

    val active: Boolean = true
)