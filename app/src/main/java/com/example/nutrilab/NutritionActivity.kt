package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NutritionActivity : AppCompatActivity() {

    private lateinit var nutritionTitle: TextView
    private lateinit var caloriesText: TextView
    private lateinit var proteinText: TextView
    private lateinit var carbsText: TextView
    private lateinit var fatText: TextView
    private lateinit var addToDailyTotalsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        nutritionTitle = findViewById(R.id.nutritionTitle)
        caloriesText = findViewById(R.id.caloriesText)
        proteinText = findViewById(R.id.proteinText)
        carbsText = findViewById(R.id.carbsText)
        fatText = findViewById(R.id.fatText)
        addToDailyTotalsButton = findViewById(R.id.addToDailyTotalsButton)

        val foodName = intent.getStringExtra("foodName") ?: "Unknown"
        val calories = intent.getIntExtra("calories", 0)
        val protein = intent.getFloatExtra("protein", 0f)
        val carbs = intent.getFloatExtra("carbs", 0f)
        val fat = intent.getFloatExtra("fat", 0f)

        nutritionTitle.text = foodName
        caloriesText.text = "Calories: $calories"
        proteinText.text = "Protein: ${protein}g"
        carbsText.text = "Carbs: ${carbs}g"
        fatText.text = "Fat: ${fat}g"

        addToDailyTotalsButton.setOnClickListener {
            val foodItem = FoodItem(
                name = foodName,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )

            val resultIntent = Intent()
            resultIntent.putExtra("newFood", foodItem)
            setResult(RESULT_OK, resultIntent)

            Toast.makeText(this, "$foodName added to daily totals!", Toast.LENGTH_SHORT).show()
            finish()
        }

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@NutritionActivity, SearchActivity::class.java))
        }
    }
}