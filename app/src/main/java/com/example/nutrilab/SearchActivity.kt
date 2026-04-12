package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsList: ListView

    private val db = FirebaseFirestore.getInstance()
    private val foodResults = ArrayList<String>()
    private val foodItemCache = ArrayList<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBar = findViewById(R.id.searchBar)
        searchButton = findViewById(R.id.searchButton)
        resultsList = findViewById(R.id.resultsList)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodResults)
        resultsList.adapter = adapter

        //back button
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        searchButton.setOnClickListener {
            val foodName = searchBar.text.toString().trim()

            if (foodName.isEmpty()) {
                Toast.makeText(this, "Enter a food name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            foodResults.clear()
            foodItemCache.clear()
            foodResults.add("Searching...")
            adapter.notifyDataSetChanged()

            // Search Open Food Facts API on background thread
            thread {
                try {
                    val encoded = URLEncoder.encode(foodName, "UTF-8")
                    val url = URL("https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encoded&search_simple=1&action=process&json=1&page_size=20")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("User-Agent", "NutriLab/1.0")
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000

                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val products = json.getJSONArray("products")

                    val tempFoods = ArrayList<FoodItem>()

                    for (i in 0 until minOf(products.length(), 15)) {
                        val product = products.getJSONObject(i)
                        val name = product.optString("product_name", "").trim()
                        if (name.isEmpty()) continue

                        val nutrients = product.optJSONObject("nutriments")
                        val calories = nutrients?.optDouble("energy-kcal_100g", 0.0)?.toInt() ?: 0
                        val protein = nutrients?.optDouble("proteins_100g", 0.0)?.toFloat() ?: 0f
                        val carbs = nutrients?.optDouble("carbohydrates_100g", 0.0)?.toFloat() ?: 0f
                        val fat = nutrients?.optDouble("fat_100g", 0.0)?.toFloat() ?: 0f

                        tempFoods.add(FoodItem(name, calories, protein, carbs, fat))
                    }

                    runOnUiThread {
                        foodResults.clear()
                        foodItemCache.clear()

                        if (tempFoods.isEmpty()) {
                            foodResults.add("No Results Found")
                        } else {
                            for (food in tempFoods) {
                                foodResults.add("${food.name} — ${food.calories} kcal")
                                foodItemCache.add(food)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    // API failed — fall back to Firestore
                    val foodNameLower = foodName.lowercase()
                    db.collection("foods")
                        .whereEqualTo("name", foodNameLower)
                        .get()
                        .addOnSuccessListener { documents ->
                            foodResults.clear()
                            foodItemCache.clear()

                            if (documents.isEmpty) {
                                foodResults.add("No Results Found")
                            } else {
                                for (doc in documents) {
                                    val name = doc.getString("name") ?: continue
                                    val calories = doc.getLong("calories")?.toInt() ?: 0
                                    val protein = doc.getDouble("protein")?.toFloat() ?: 0f
                                    val carbs = doc.getDouble("carbs")?.toFloat() ?: 0f
                                    val fat = doc.getDouble("fat")?.toFloat() ?: 0f
                                    foodResults.add("$name — $calories kcal")
                                    foodItemCache.add(FoodItem(name, calories, protein, carbs, fat))
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }
                }
            }
        }

        resultsList.setOnItemClickListener { _, _, position, _ ->
            val selected = foodResults[position]
            if (selected == "No Results Found" || selected == "Searching...") return@setOnItemClickListener

            if (position < foodItemCache.size) {
                val foodItem = foodItemCache[position]

                val resultIntent = Intent()
                resultIntent.putExtra("newFood", foodItem)
                setResult(RESULT_OK, resultIntent)

                val intent = Intent(this, NutritionActivity::class.java)
                intent.putExtra("foodName", foodItem.name)
                intent.putExtra("calories", foodItem.calories)
                intent.putExtra("protein", foodItem.protein)
                intent.putExtra("carbs", foodItem.carbs)
                intent.putExtra("fat", foodItem.fat)
                startActivity(intent)
            }
        }
    }
}