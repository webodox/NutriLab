package com.example.nutrilab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutrilab.data.entity.SessionEntity

@Dao
interface SessionDao {

    @Insert
    suspend fun createSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("UPDATE sessions SET isActive = 0 WHERE isActive = 1")
    suspend fun logout()
}