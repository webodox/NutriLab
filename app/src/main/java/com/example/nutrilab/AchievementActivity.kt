package com.example.nutrilab

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.sql.Time

object AchievementActivity {
    private val db = FirebaseFirestore.getInstance()

    fun getBadge(points: Int): String {
        return when {
            points >= 15000 -> "diamond"
            points >= 10000 -> "platinum"
            points >= 5000 -> "gold"
            points >= 1000 -> "silver"
            else -> "bronze"
        }
    }

    private fun updateAchievement(userId: String, points: Int, badge: String) {
        db.collection("achievements").document(userId)
            .set(
                hashMapOf(
                    "userId" to userId,
                    "points" to points,
                    "badge" to badge,
                    "date" to Timestamp.now()
                ),
                SetOptions.merge()
            )
            .addOnFailureListener { e ->
                Log.e("AchievementActivity", "Failed to updated achievement.", e)
            }
    }

    fun onAccountCreated(userId: String) {
        db.collection("users").document(userId)
            .set(
                mapOf("mealsLogged" to 0),
                SetOptions.merge()
            )
            .addOnFailureListener { e ->
                Log.e("AchievementActivity", "Failed to initialize user", e)
            }

        updateAchievement(userId, 100, getBadge(100))
    }

    fun onMealLogged(userId: String) {
        val userRef = db.collection("users").document(userId)
        val achievementRef = db.collection("achievements").document(userId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val achievementSnapshot = transaction.get(achievementRef)
            val mealsLogged = userSnapshot.getLong("mealsLogged")?.toInt() ?: 0
            val currentPoints = achievementSnapshot.getLong("points")?.toInt() ?: 0

            val newMealsLogged = mealsLogged + 1
            val pointsToAdd = if (newMealsLogged % 10 == 0) 100 else 0

            val newPoints = currentPoints + pointsToAdd
            val newBadge = getBadge(newPoints)

            transaction.update(userRef, "mealsLogged", newMealsLogged)

            if (pointsToAdd > 0) {
                transaction.set(
                    achievementRef,
                    hashMapOf(
                        "userId" to userId,
                        "points" to newPoints,
                        "badge" to newBadge,
                        "date" to Timestamp.now()
                    ),
                    SetOptions.merge()
                )
            }

            Triple(newPoints, newBadge, pointsToAdd)
        }
        .addOnSuccessListener { (newPoints, newBadge, pointsToAdd) ->
        Log.d("AchievementActivity", "Meal logged. Points: $newPoints, Awarded: $pointsToAdd, Badge: $newBadge")
        }
        .addOnFailureListener { e ->
            Log.e("AchievementActivity", "Failed to log meal points.", e)
        }
    }
}
