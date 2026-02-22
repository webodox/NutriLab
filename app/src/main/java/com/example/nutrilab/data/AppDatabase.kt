package com.example.nutrilab.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutrilab.data.dao.SessionDao
import com.example.nutrilab.data.dao.UserDao
import com.example.nutrilab.data.entity.SessionEntity
import com.example.nutrilab.data.entity.UserEntity

@Database(
    entities = [UserEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrilab.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}