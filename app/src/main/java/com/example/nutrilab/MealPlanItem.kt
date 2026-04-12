package com.example.nutrilab

import java.util.Date

//format for meal plan in database -mati sawadogo
data class MealPlanItem(
    val id: String = "",
    val mealType: String = "",
    val foods: List<MealPlanFoodItem> = emptyList(),
    val startDate : Date? = null,
    val endDate : Date? = null
)