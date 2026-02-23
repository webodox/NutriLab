package com.example.nutrilab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutrilab.data.entity.UserGoalEntity

@Dao
interface UserGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGoal(userGoal: UserGoalEntity): Long

    @Query("SELECT * FROM user_goals WHERE userId = :userId AND active = 1")
    suspend fun getActiveGoalsForUser(userId: Int): List<UserGoalEntity>

    @Query("UPDATE user_goals SET active = 0 WHERE userId = :userId")
    suspend fun deactivateGoalsForUser(userId: Int)
}