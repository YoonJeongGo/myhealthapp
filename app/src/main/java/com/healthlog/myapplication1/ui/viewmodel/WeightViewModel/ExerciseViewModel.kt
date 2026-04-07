package com.healthlog.myapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import com.healthlog.myapplication1.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<String>("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _exerciseFlow = MutableStateFlow<Flow<List<ExerciseRecordEntity>>?>(null)
    val exerciseFlow: StateFlow<Flow<List<ExerciseRecordEntity>>?> = _exerciseFlow.asStateFlow()

    fun saveExercise(
        date: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int
    ) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.insertExercise(
                    date = date,
                    type = type,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned
                )
                _saveState.value = "success"
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun loadExercisesByDate(date: String) {
        try {
            _exerciseFlow.value = repository.getExercisesByDate(date)
        } catch (e: Exception) {
            _saveState.value = "error: ${e.message}"
        }
    }

    fun resetState() {
        _saveState.value = "idle"
    }
}