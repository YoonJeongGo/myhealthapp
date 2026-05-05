package com.healthlog.myapplication1.data.remote.dto

data class ParsedMealItem(
    val name: String,
    val calories: Int,
    val unit: String? = null,
    val isEstimated: Boolean = true
)

data class MealCalorieResponse(
    val items: List<ParsedMealItem>,
    val totalCalories: Int
)
