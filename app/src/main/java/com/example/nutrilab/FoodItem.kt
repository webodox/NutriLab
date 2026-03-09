package com.example.nutrilab

import java.io.Serializable

data class FoodItem(
    val name: String,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f
) : Serializable