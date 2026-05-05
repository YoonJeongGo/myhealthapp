package com.healthlog.myapplication1.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.repository.BodyFatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BodyFatInputViewModel(private val repo: BodyFatRepository) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _previousBodyFat = MutableStateFlow<Float?>(null)
    val previousBodyFat: StateFlow<Float?> = _previousBodyFat.asStateFlow()

    init {
        viewModelScope.launch {
            _previousBodyFat.value = repo.getLatestBodyFat()?.bodyFat
        }
    }

    fun save(date: String, bodyFat: Float, note: String? = null) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            try {
                repo.insertBodyFat(date, bodyFat, note = note)
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "오류")
            }
        }
    }

    fun reset() { _saveState.value = SaveState.Idle }

    class Factory(private val repo: BodyFatRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = BodyFatInputViewModel(repo) as T
    }
}
