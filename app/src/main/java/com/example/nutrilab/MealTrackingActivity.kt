package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MealTrackingActivity : AppCompatActivity() {
    private lateinit var addFoodButton: Button

    private val ADD_FOOD_REQUEST = 1
    private lateinit var searchFoodButton: Button
    private lateinit var foodListView: ListView
    private lateinit var totalCaloriesText: TextView
    private lateinit var totalProteinText: TextView
    private lateinit var totalCarbsText: TextView
    private lateinit var totalFatText: TextView

    private val selectedFoods = ArrayList<FoodItem>()
    private lateinit var adapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_tracking)

        adapter = FoodAdapter(this, selectedFoods)
        addFoodButton = findViewById(R.id.addFoodButton)
        searchFoodButton = findViewById(R.id.searchFoodButton)
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
    }

    // Handle result from SearchActivity or AddFoodActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100){
            val newFood = data?.getSerializableExtra("newFood") as? FoodItem

            newFood?.let {
                selectedFoods.add(it)

                adapter.notifyDataSetChanged()

                updateTotals()   //  THIS updates the nutrition totals
            }
        }

        if (requestCode == ADD_FOOD_REQUEST && resultCode == RESULT_OK) {

            val newFood = data?.getSerializableExtra("newFood") as? FoodItem

            newFood?.let {
                selectedFoods.add(it)

                adapter.notifyDataSetChanged()

                updateTotals()   //  THIS updates the nutrition totals
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
}
