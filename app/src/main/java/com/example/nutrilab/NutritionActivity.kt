package com.example.nutrilab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NutritionActivity : AppCompatActivity() {

    private lateinit var nutritionTitle: TextView
    private lateinit var caloriesText: TextView
    private lateinit var proteinText: TextView
    private lateinit var carbsText: TextView
    private lateinit var fatText: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        nutritionTitle = findViewById(R.id.nutritionTitle)
        caloriesText = findViewById(R.id.caloriesText)
        proteinText = findViewById(R.id.proteinText)
        carbsText = findViewById(R.id.carbsText)
        fatText = findViewById(R.id.fatText)

        val foodName = intent.getStringExtra("foodName")
        nutritionTitle.text = foodName

        if (foodName != null) {
            fetchNutritionData(foodName)
        }
    }

    private fun fetchNutritionData(foodName: String) {
        db.collection("foods")
            .whereEqualTo("name", foodName)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val calories = doc.getLong("calories") ?: 0
                    val protein = doc.getLong("protein") ?: 0
                    val carbs = doc.getLong("carbs") ?: 0
                    val fat = doc.getLong("fat") ?: 0

                    caloriesText.text = "Calories: $calories"
                    proteinText.text = "Protein: ${protein}g"
                    carbsText.text = "Carbs: ${carbs}g"
                    fatText.text = "Fat: ${fat}g"
                }
            }
    }
}
