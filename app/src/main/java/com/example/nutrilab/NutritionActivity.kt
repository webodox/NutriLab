package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NutritionActivity : AppCompatActivity() {

    private lateinit var nutritionTitle: TextView
    private lateinit var caloriesText: TextView
    private lateinit var proteinText: TextView
    private lateinit var carbsText: TextView
    private lateinit var fatText: TextView

    private lateinit var addToDailyTotalsButton: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        nutritionTitle = findViewById(R.id.nutritionTitle)
        caloriesText = findViewById(R.id.caloriesText)
        proteinText = findViewById(R.id.proteinText)
        carbsText = findViewById(R.id.carbsText)
        fatText = findViewById(R.id.fatText)
        addToDailyTotalsButton = findViewById(R.id.addToDailyTotalsButton)

        addToDailyTotalsButton.setOnClickListener {

            // Build the FoodItem
            val foodItem = FoodItem(
                name = nutritionTitle.text.toString(),
                calories = caloriesText.text.toString().replace("Calories: ", "").toIntOrNull() ?: 0,
                protein = proteinText.text.toString().replace("Protein: ", "").replace("g", "").toFloatOrNull() ?: 0f,
                carbs = carbsText.text.toString().replace("Carbs: ", "").replace("g", "").toFloatOrNull() ?: 0f,
                fat = fatText.text.toString().replace("Fat: ", "").replace("g", "").toFloatOrNull() ?: 0f
            )

            // Send it back to MealTrackingActivity without closing the page
            val resultIntent = Intent()
            resultIntent.putExtra("newFood", foodItem)
            setResult(RESULT_OK, resultIntent)

            Toast.makeText(this, "${foodItem.name} added to daily totals!", Toast.LENGTH_SHORT).show()
            finish()
        }

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
