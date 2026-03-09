package com.example.nutrilab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FoodAdapter(private val context: Context, private val foods: List<FoodItem>) : BaseAdapter() {

    override fun getCount(): Int = foods.size
    override fun getItem(position: Int): Any = foods[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.food_list_item, parent, false)

        val foodName = view.findViewById<TextView>(R.id.foodName)
        val foodCalories = view.findViewById<TextView>(R.id.foodCalories)

        val food = foods[position]
        foodName.text = food.name
        foodCalories.text = "Calories: ${food.calories}"

        return view
    }
}