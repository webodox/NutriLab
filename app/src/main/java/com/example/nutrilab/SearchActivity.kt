package com.example.nutrilab

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsList: ListView

    private val db = FirebaseFirestore.getInstance()
    private val foodResults = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBar = findViewById(R.id.searchBar)
        searchButton = findViewById(R.id.searchButton)
        resultsList = findViewById(R.id.resultsList)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, foodResults)
        resultsList.adapter = adapter

        searchButton.setOnClickListener {

            val foodName = searchBar.text.toString().trim()

            if (foodName.isEmpty()) {
                Toast.makeText(this, "Enter a food name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("foods")
                .whereEqualTo("name", foodName)
                .get()
                .addOnSuccessListener { documents ->

                    foodResults.clear()

                    if (documents.isEmpty) {
                        foodResults.add("No Results Found")
                    } else {
                        for (doc in documents) {
                            val name = doc.getString("name")
                            if (name != null) {
                                foodResults.add(name)
                            }
                        }
                    }

                    adapter.notifyDataSetChanged()
                }
        }

        resultsList.setOnItemClickListener { _, _, position, _ ->

            val selectedFood = foodResults[position]

            if (selectedFood == "No Results Found") return@setOnItemClickListener

            val intent = Intent(this, NutritionActivity::class.java)
            intent.putExtra("foodName", selectedFood)
            startActivity(intent)
        }
    }
}