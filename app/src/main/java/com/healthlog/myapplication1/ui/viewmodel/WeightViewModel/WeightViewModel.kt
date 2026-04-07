package com.healthlog.myapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity
import com.healthlog.myapplication1.data.repository.WeightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<String>("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _latestWeight = MutableStateFlow<WeightLogEntity?>(null)
    val latestWeight: StateFlow<WeightLogEntity?> = _latestWeight.asStateFlow()

    fun saveWeight(
        date: String,
        weight: Float,
        note: String? = null
    ) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.insertWeight(
                    date = date,
                    weight = weight,
                    note = note
                )
                _saveState.value = "success"
                _latestWeight.value = repository.getLatestWeight()
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun loadLatestWeight() {
        viewModelScope.launch {
            try {
                _latestWeight.value = repository.getLatestWeight()
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun resetState() {
        _saveState.value = "idle"
    }
}