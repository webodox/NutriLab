package com.example.nutrilab.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutrilab.data.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?
}