package com.example.nutrilab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddFoodActivity : AppCompatActivity() {

    private lateinit var foodNameEdit: EditText
    private lateinit var caloriesEdit: EditText
    private lateinit var proteinEdit: EditText
    private lateinit var carbsEdit: EditText
    private lateinit var fatEdit: EditText
    private lateinit var addButton: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        // Initialize views
        foodNameEdit = findViewById(R.id.foodNameEdit)
        caloriesEdit = findViewById(R.id.caloriesEdit)
        proteinEdit = findViewById(R.id.proteinEdit)
        carbsEdit = findViewById(R.id.carbsEdit)
        fatEdit = findViewById(R.id.fatEdit)
        addButton = findViewById(R.id.addButton)

        addButton.setOnClickListener {
            addFood()

        }
        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this@AddFoodActivity, MealTrackingActivity::class.java))
        }
    }

    private fun addFood() {
        // Get text from fields
        val name = foodNameEdit.text.toString().trim().lowercase()
        val caloriesText = caloriesEdit.text.toString().trim()
        val proteinText = proteinEdit.text.toString().trim()
        val carbsText = carbsEdit.text.toString().trim()
        val fatText = fatEdit.text.toString().trim()

        // Validate required fields
        if (name.isEmpty()) {
            Toast.makeText(this, "Food name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (caloriesText.isEmpty()) {
            Toast.makeText(this, "Calories are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse optional fields safely
        val calories = caloriesText.toIntOrNull() ?: 0
        val protein = proteinText.toFloatOrNull() ?: 0f
        val carbs = carbsText.toFloatOrNull() ?: 0f
        val fat = fatText.toFloatOrNull() ?: 0f

        // Create FoodItem
        val foodItem = FoodItem(
            name = name,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat
        )

        // Save food to Firestore
        val foodMap = hashMapOf(
            "name" to name,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat
        )

        db.collection("foods")
            .add(foodMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Food saved to database!", Toast.LENGTH_SHORT).show()

                // Return FoodItem to MealTrackingActivity
                val resultIntent = Intent()
                resultIntent.putExtra("newFood", foodItem)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save food", Toast.LENGTH_SHORT).show()
            }
    }
}