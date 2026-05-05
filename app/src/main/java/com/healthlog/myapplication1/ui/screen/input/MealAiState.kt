package com.healthlog.myapplication1.ui.screen.input

import com.healthlog.myapplication1.data.remote.dto.ErrorType
import com.healthlog.myapplication1.data.remote.dto.ParsedMealItem

sealed class MealAiState {
    object Idle : MealAiState()
    object Loading : MealAiState()
    data class Success(
        val items: List<ParsedMealItem>,
        val totalCalories: Int
    ) : MealAiState()
    data class Error(
        val type: ErrorType,
        val rawText: String? = null
    ) : MealAiState()
}
