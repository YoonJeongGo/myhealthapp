package com.healthlog.myapplication1.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.*
import com.healthlog.myapplication1.data.repository.*
import com.healthlog.myapplication1.domain.util.BmrCalculator
import com.healthlog.myapplication1.domain.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: UserProfileEntity? = null,
    val todayLog: DailyLogEntity? = null,
    val todayMeals: List<MealRecordWithItems> = emptyList(),
    val todayExercises: List<ExerciseRecordEntity> = emptyList(),
    val targetCalories: Int = 0,
    val date: String = DateUtils.today()
)

class HomeViewModel(
    private val userRepo: UserRepository,
    private val weightRepo: WeightRepository,
    private val mealRepo: MealRepository,
    private val exerciseRepo: ExerciseRepository,
    private val dailyLogRepo: DailyLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadToday() }

    fun loadToday() {
        val date = DateUtils.today()
        viewModelScope.launch {
            val profile = userRepo.getProfile()
            val target = profile?.let {
                BmrCalculator.targetCalories(it.tdee, it.goal).toInt()
            } ?: 2000

            dailyLogRepo.getOrCreate(date)

            combine(
                dailyLogRepo.getRange(date, date),
                mealRepo.getMealsByDate(date),
                exerciseRepo.getExercisesByDate(date)
            ) { logs, meals, exercises ->
                HomeUiState(
                    profile = profile,
                    todayLog = logs.firstOrNull(),
                    todayMeals = meals,
                    todayExercises = exercises,
                    targetCalories = target,
                    date = date
                )
            }.collect { _uiState.value = it }
        }
    }

    class Factory(
        private val userRepo: UserRepository,
        private val weightRepo: WeightRepository,
        private val mealRepo: MealRepository,
        private val exerciseRepo: ExerciseRepository,
        private val dailyLogRepo: DailyLogRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            HomeViewModel(userRepo, weightRepo, mealRepo, exerciseRepo, dailyLogRepo) as T
    }
}
