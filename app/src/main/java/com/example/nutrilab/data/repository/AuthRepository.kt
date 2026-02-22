package com.example.nutrilab.data.repository

import com.example.nutrilab.data.dao.SessionDao
import com.example.nutrilab.data.dao.UserDao
import com.example.nutrilab.data.entity.SessionEntity
import com.example.nutrilab.data.entity.UserEntity
import java.util.UUID

class AuthRepository(
    private val userDao: UserDao,
    private val sessionDao: SessionDao
) {
    suspend fun register(user: UserEntity): Result<Long> {
        val existing = userDao.getUserByEmail(user.email)
        if (existing != null) return Result.failure(Exception("Email already exists"))
        val id = userDao.insertUser(user)
        return Result.success(id)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.login(email, password)
            ?: return Result.failure(Exception("Invalid credentials"))
        sessionDao.logout() // close any previous active session
        sessionDao.createSession(
            SessionEntity(userId = user.userId, token = UUID.randomUUID().toString())
        )
        return Result.success(user)
    }

    suspend fun logout() {
        sessionDao.logout()
    }

    suspend fun hasActiveSession(): Boolean {
        return sessionDao.getActiveSession() != null
    }
}