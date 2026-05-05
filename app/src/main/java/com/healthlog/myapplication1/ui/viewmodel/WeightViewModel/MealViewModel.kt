package com.healthlog.myapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.MealRecordWithItems
import com.healthlog.myapplication1.data.repository.MealItemInput
import com.healthlog.myapplication1.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    private val _saveState = MutableStateFlow("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _mealsByDate = MutableStateFlow<List<MealRecordWithItems>>(emptyList())
    val mealsByDate: StateFlow<List<MealRecordWithItems>> = _mealsByDate.asStateFlow()

    fun saveMeal(date: String, mealType: String, items: List<MealItemInput>) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.saveMealWithItems(date = date, mealType = mealType, items = items)
                _saveState.value = "success"
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun loadMealsByDate(date: String) {
        viewModelScope.launch {
            repository.getMealsByDate(date).collect { _mealsByDate.value = it }
        }
    }

    fun resetState() { _saveState.value = "idle" }
}
