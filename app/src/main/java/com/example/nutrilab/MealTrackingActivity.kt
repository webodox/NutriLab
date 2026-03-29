package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MealTrackingActivity : AppCompatActivity() {

    private lateinit var addFoodButton: Button
    private lateinit var searchFoodButton: Button
    private lateinit var submitMealButton: Button
    private lateinit var foodListView: ListView
    private lateinit var totalCaloriesText: TextView
    private lateinit var totalProteinText: TextView
    private lateinit var totalCarbsText: TextView
    private lateinit var totalFatText: TextView

    private val ADD_FOOD_REQUEST = 1
    private val selectedFoods = ArrayList<FoodItem>()
    private lateinit var adapter: FoodAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_tracking)

        adapter = FoodAdapter(this, selectedFoods)
        addFoodButton = findViewById(R.id.addFoodButton)
        searchFoodButton = findViewById(R.id.searchFoodButton)
        submitMealButton = findViewById(R.id.submitMealButton)
        foodListView = findViewById(R.id.foodListView)
        totalCaloriesText = findViewById(R.id.totalCaloriesText)
        totalProteinText = findViewById(R.id.totalProteinText)
        totalCarbsText = findViewById(R.id.totalCarbsText)
        totalFatText = findViewById(R.id.totalFatText)

        foodListView.adapter = adapter

        searchFoodButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, 100)
        }

        addFoodButton.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivityForResult(intent, ADD_FOOD_REQUEST)
        }

        submitMealButton.setOnClickListener {
            saveMealToFirestore()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100) {
            val newFood = data?.getSerializableExtra("newFood") as? FoodItem
            newFood?.let {
                selectedFoods.add(it)
                adapter.notifyDataSetChanged()
                updateTotals()
            }
        }

        if (requestCode == ADD_FOOD_REQUEST && resultCode == RESULT_OK) {
            val newFood = data?.getSerializableExtra("newFood") as? FoodItem
            newFood?.let {
                selectedFoods.add(it)
                adapter.notifyDataSetChanged()
                updateTotals()
            }
        }
    }

    private fun updateTotals() {
        val totalCalories = selectedFoods.sumOf { it.calories }
        val totalProtein = selectedFoods.sumOf { it.protein.toDouble() }
        val totalCarbs = selectedFoods.sumOf { it.carbs.toDouble() }
        val totalFat = selectedFoods.sumOf { it.fat.toDouble() }

        totalCaloriesText.text = "Calories: $totalCalories"
        totalProteinText.text = "Protein: ${totalProtein}g"
        totalCarbsText.text = "Carbs: ${totalCarbs}g"
        totalFatText.text = "Fat: ${totalFat}g"
    }

    private fun saveMealToFirestore() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedFoods.isEmpty()) {
            Toast.makeText(this, "Please add at least one food item.", Toast.LENGTH_SHORT).show()
            return
        }

        val totalCalories = selectedFoods.sumOf { it.calories }
        val totalProtein = selectedFoods.sumOf { it.protein.toDouble() }
        val totalCarbs = selectedFoods.sumOf { it.carbs.toDouble() }
        val totalFat = selectedFoods.sumOf { it.fat.toDouble() }

        val foodNames = selectedFoods.map { it.name }

        val mealLog = hashMapOf(
            "userId" to userId,
            "foods" to foodNames,
            "totalCalories" to totalCalories,
            "totalProtein" to totalProtein,
            "totalCarbs" to totalCarbs,
            "totalFat" to totalFat,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        db.collection("mealLogs")
            .add(mealLog)
            .addOnSuccessListener {
                Toast.makeText(this, "Meal saved successfully!", Toast.LENGTH_SHORT).show()
                selectedFoods.clear()
                adapter.notifyDataSetChanged()
                updateTotals()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save meal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}