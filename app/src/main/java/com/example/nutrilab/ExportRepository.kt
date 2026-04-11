package com.example.nutrilab


import com.google.firebase.firestore.FirebaseFirestore

//set up for .txt file of data
class ExportRepository {
    private val db = FirebaseFirestore.getInstance()

    fun exportAllUserData(userId: String, onComplete: (String) -> Unit) {
        val result = StringBuilder()

        //meal plan history
        db.collection("mealPlanLog")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { mealPlans ->
                result.append("    MEAL PLANS:    \n\n")

                for (doc in mealPlans) {
                    val mealType = doc.getString("mealType") ?: "Unknown"
                    val foods = doc.get("foods") as? List<Map<String, Any>> ?: emptyList()
                    val startDate = doc.getDate("startDate") ?: ""
                    val endDate = doc.getDate("endDate") ?: ""
                    result.append("\nMeal Type: $mealType\n")
                    result.append("Date Range: $startDate - $endDate\n\n")

                    for (food in foods) {
                        val name = food["name"] as? String ?: ""
                        val grams = food["grams"] as? Long ?: 0
                        result.append(" • $name ($grams g)\n")
                    }
                }
                result.append("--------------------------------------------------------------------------------------\n")
                exportMealTracking(userId, result, onComplete)
            }
    }

    //meal tracking history
    private fun exportMealTracking(userId: String, result: StringBuilder, onComplete: (String) -> Unit) {
        db.collection("mealLogs")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { meals ->
                result.append("    MEAL TRACKING:    \n")

                for (doc in meals) {
                    val meal = doc.get("foods") as? List<String> ?: emptyList()
                    val calories = doc.getLong("totalCalories") ?: 0
                    val carbs = doc.getLong("totalCarbs") ?: 0
                    val fat = doc.getLong("totalFat") ?: 0
                    val protein = doc.getLong("totalProtein") ?: 0
                    val date = doc.getDate("timestamp") ?: ""
                    result.append("\nDate: $date \n")

                    for (food in meal) {
                        result.append("• ${food}\n")
                    }
                    result.append("• Calories: $calories kcal | Carbs: $carbs g | Fat: $fat g | Protein: $protein g\n")
                }
                result.append("--------------------------------------------------------------------------------------\n")
                exportWaterIntake(userId, result, onComplete)
            }
    }

    //water intake history
    private fun exportWaterIntake(userId: String, result: StringBuilder, onComplete: (String) -> Unit) {
        db.collection("waterLogs")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { water ->
                result.append("    WATER INTAKE:    \n")

                for (doc in water) {
                    val amount = doc.getLong("amount") ?: 0
                    val date = doc.getDate("timestamp") ?: ""
                    result.append("\nDate: $date\n")
                    result.append("• ${amount} oz\n")
                }

                result.append("\n\n Export complete.")
                onComplete(result.toString())
            }
    }
}
