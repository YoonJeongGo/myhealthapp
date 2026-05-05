package com.healthlog.myapplication1.ui.screen.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.remote.dto.ErrorType
import com.healthlog.myapplication1.data.repository.AiException
import com.healthlog.myapplication1.data.repository.AiRepository
import com.healthlog.myapplication1.data.repository.MealItemInput
import com.healthlog.myapplication1.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealInputViewModel(
    private val mealRepo: MealRepository,
    private val aiRepo: AiRepository
) : ViewModel() {

    private val _aiState = MutableStateFlow<MealAiState>(MealAiState.Idle)
    val aiState: StateFlow<MealAiState> = _aiState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    fun analyzeWithAi(rawInput: String) {
        if (rawInput.isBlank()) return
        viewModelScope.launch {
            _aiState.value = MealAiState.Loading
            aiRepo.estimateMealCalories(rawInput)
                .onSuccess { response ->
                    _aiState.value = MealAiState.Success(
                        items = response.items,
                        totalCalories = response.totalCalories
                    )
                }
                .onFailure { e ->
                    val ex = e as? AiException
                    _aiState.value = MealAiState.Error(
                        type = ex?.type ?: ErrorType.UNKNOWN,
                        rawText = ex?.rawText
                    )
                }
        }
    }

    fun saveMeal(
        date: String,
        mealType: String,
        rawInput: String,
        items: List<MealItemInput>,
        isAiEstimated: Boolean
    ) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            try {
                mealRepo.saveMealWithItems(
                    date = date,
                    mealType = mealType,
                    rawInput = rawInput,
                    items = items,
                    isAiEstimated = isAiEstimated
                )
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "오류")
            }
        }
    }

    fun resetAiState() { _aiState.value = MealAiState.Idle }
    fun resetSaveState() { _saveState.value = SaveState.Idle }

    class Factory(
        private val mealRepo: MealRepository,
        private val aiRepo: AiRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            MealInputViewModel(mealRepo, aiRepo) as T
    }
}
