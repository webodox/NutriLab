package com.example.nutrilab


import android.util.Log
import android.widget.Toast
import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

//tracking achievements -mati sawadogo
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

    //update achievements everytime user logs a meal
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

    //+100 achievement points for creating an account
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

    //achievement points for logging a meal
    fun onMealLogged(context: Context, userId: String) {
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
            if (pointsToAdd > 0) {
                Toast.makeText(context,"+$pointsToAdd points!", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { e ->
            Log.e("AchievementActivity", "Failed to log meal points.", e)
        }
    }
}
