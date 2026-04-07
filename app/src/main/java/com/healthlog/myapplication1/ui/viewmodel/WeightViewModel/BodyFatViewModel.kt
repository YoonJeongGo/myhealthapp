package com.healthlog.myapplication1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.repository.BodyFatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BodyFatViewModel(
    private val repository: BodyFatRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<String>("idle")
    val saveState: StateFlow<String> = _saveState.asStateFlow()

    private val _latestBodyFat = MutableStateFlow<BodyFatLogEntity?>(null)
    val latestBodyFat: StateFlow<BodyFatLogEntity?> = _latestBodyFat.asStateFlow()

    fun saveBodyFat(
        date: String,
        bodyFat: Float,
        note: String? = null
    ) {
        viewModelScope.launch {
            try {
                _saveState.value = "loading"
                repository.insertBodyFat(
                    date = date,
                    bodyFat = bodyFat,
                    note = note
                )
                _saveState.value = "success"
                _latestBodyFat.value = repository.getLatestBodyFat()
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun loadLatestBodyFat() {
        viewModelScope.launch {
            try {
                _latestBodyFat.value = repository.getLatestBodyFat()
            } catch (e: Exception) {
                _saveState.value = "error: ${e.message}"
            }
        }
    }

    fun resetState() {
        _saveState.value = "idle"
    }
}