package com.healthlog.myapplication1.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.repository.WeightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeightInputViewModel(private val repo: WeightRepository) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _previousWeight = MutableStateFlow<Float?>(null)
    val previousWeight: StateFlow<Float?> = _previousWeight.asStateFlow()

    init {
        viewModelScope.launch {
            _previousWeight.value = repo.getLatestWeight()?.weight
        }
    }

    fun save(date: String, weight: Float, note: String? = null) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            try {
                repo.insertWeight(date, weight, note = note)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "오류")
            }
        }
    }

    fun reset() { _saveState.value = SaveState.Idle }

    class Factory(private val repo: WeightRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = WeightInputViewModel(repo) as T
    }
}
