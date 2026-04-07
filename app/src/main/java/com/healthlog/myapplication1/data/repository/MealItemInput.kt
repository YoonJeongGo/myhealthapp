package com.healthlog.myapplication1.data.repository

data class MealItemInput(
    val name: String,
    val calories: Int,
    val amount: String? = null,
    val isEstimated: Boolean = true
)