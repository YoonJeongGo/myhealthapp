package com.healthlog.myapplication1.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseInputViewModel(private val repo: ExerciseRepository) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun save(date: String, exerciseName: String, duration: Int, calories: Int, note: String? = null) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            try {
                repo.insertExercise(date, exerciseName, duration, calories, note)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "오류")
            }
        }
    }

    fun reset() { _saveState.value = SaveState.Idle }

    class Factory(private val repo: ExerciseRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = ExerciseInputViewModel(repo) as T
    }
}
