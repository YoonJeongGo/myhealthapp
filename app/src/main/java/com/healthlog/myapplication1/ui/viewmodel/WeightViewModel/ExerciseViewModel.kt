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

    private val _saveState = MutableStateFlow("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _exerciseFlow = MutableStateFlow<Flow<List<ExerciseRecordEntity>>?>(null)
    val exerciseFlow: StateFlow<Flow<List<ExerciseRecordEntity>>?> = _exerciseFlow.asStateFlow()

    fun saveExercise(
        date: String,
        exerciseName: String,
        duration: Int,
        calories: Int,
        note: String? = null
    ) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.insertExercise(
                    date = date,
                    exerciseName = exerciseName,
                    duration = duration,
                    calories = calories,
                    note = note
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