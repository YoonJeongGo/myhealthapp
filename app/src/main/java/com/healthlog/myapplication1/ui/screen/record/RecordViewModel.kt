package com.healthlog.myapplication1.ui.screen.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.*
import com.healthlog.myapplication1.data.repository.*
import com.healthlog.myapplication1.domain.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RecordUiState(
    val date: String = DateUtils.today(),
    val weights: List<WeightLogEntity> = emptyList(),
    val bodyFats: List<BodyFatLogEntity> = emptyList(),
    val meals: List<MealRecordWithItems> = emptyList(),
    val exercises: List<ExerciseRecordEntity> = emptyList()
)

class RecordViewModel(
    private val weightRepo: WeightRepository,
    private val bodyFatRepo: BodyFatRepository,
    private val mealRepo: MealRepository,
    private val exerciseRepo: ExerciseRepository
) : ViewModel() {

    private val _date = MutableStateFlow(DateUtils.today())
    val date: StateFlow<String> = _date.asStateFlow()

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    init { loadForDate(DateUtils.today()) }

    fun loadForDate(date: String) {
        _date.value = date
        viewModelScope.launch {
            combine(
                weightRepo.getWeightsByDate(date),
                bodyFatRepo.getBodyFatByDate(date),
                mealRepo.getMealsByDate(date),
                exerciseRepo.getExercisesByDate(date)
            ) { w, bf, m, e ->
                RecordUiState(date = date, weights = w, bodyFats = bf, meals = m, exercises = e)
            }.collect { _uiState.value = it }
        }
    }

    fun deleteWeight(entity: WeightLogEntity) {
        viewModelScope.launch { weightRepo.deleteWeight(entity) }
    }

    fun deleteBodyFat(entity: BodyFatLogEntity) {
        viewModelScope.launch { bodyFatRepo.deleteBodyFat(entity) }
    }

    fun deleteMealRecord(entity: MealRecordEntity) {
        viewModelScope.launch { mealRepo.deleteMealRecord(entity) }
    }

    fun deleteExercise(entity: ExerciseRecordEntity) {
        viewModelScope.launch { exerciseRepo.deleteExercise(entity) }
    }

    class Factory(
        private val weightRepo: WeightRepository,
        private val bodyFatRepo: BodyFatRepository,
        private val mealRepo: MealRepository,
        private val exerciseRepo: ExerciseRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            RecordViewModel(weightRepo, bodyFatRepo, mealRepo, exerciseRepo) as T
    }
}
