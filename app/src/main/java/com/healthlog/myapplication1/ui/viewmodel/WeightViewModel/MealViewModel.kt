package com.healthlog.myapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity
import com.healthlog.myapplication1.data.repository.MealItemInput
import com.healthlog.myapplication1.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(
    private val repository: MealRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<String>("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _mealsByDate = MutableStateFlow<List<MealRecordEntity>>(emptyList())
    val mealsByDate: StateFlow<List<MealRecordEntity>> = _mealsByDate.asStateFlow()

    fun saveMeal(
        date: String,
        mealType: String,
        items: List<MealItemInput>
    ) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.saveMeal(
                    date = date,
                    mealType = mealType,
                    items = items
                )
                _mealsByDate.value = repository.getMealsByDate(date)
                _saveState.value = "success"
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun loadMealsByDate(date: String) {
        viewModelScope.launch {
            try {
                _mealsByDate.value = repository.getMealsByDate(date)
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun resetState() {
        _saveState.value = "idle"
    }
}