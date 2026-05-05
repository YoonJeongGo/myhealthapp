package com.healthlog.myapplication1.ui.screen.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity
import com.healthlog.myapplication1.data.repository.BodyFatRepository
import com.healthlog.myapplication1.data.repository.WeightRepository
import com.healthlog.myapplication1.domain.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class GraphCategory { WEIGHT, BODY_FAT }
enum class GraphPeriod(val label: String) { D7("7일"), M1("1개월"), Y1("1년") }

data class GraphUiState(
    val category: GraphCategory = GraphCategory.WEIGHT,
    val period: GraphPeriod = GraphPeriod.D7,
    val weights: List<WeightLogEntity> = emptyList(),
    val bodyFats: List<BodyFatLogEntity> = emptyList()
)

class GraphViewModel(
    private val weightRepo: WeightRepository,
    private val bodyFatRepo: BodyFatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GraphUiState())
    val uiState: StateFlow<GraphUiState> = _uiState.asStateFlow()

    init { load() }

    fun setCategory(cat: GraphCategory) {
        _uiState.value = _uiState.value.copy(category = cat)
        load()
    }

    fun setPeriod(p: GraphPeriod) {
        _uiState.value = _uiState.value.copy(period = p)
        load()
    }

    private fun load() {
        val (from, to) = periodRange(_uiState.value.period)
        viewModelScope.launch {
            weightRepo.getLatestPerDayInRange(from, to).collect { list ->
                _uiState.value = _uiState.value.copy(weights = list)
            }
        }
        viewModelScope.launch {
            bodyFatRepo.getLatestPerDayInRange(from, to).collect { list ->
                _uiState.value = _uiState.value.copy(bodyFats = list)
            }
        }
    }

    private fun periodRange(p: GraphPeriod): Pair<String, String> {
        val to = DateUtils.today()
        val from = when (p) {
            GraphPeriod.D7 -> DateUtils.nDaysAgo(6)
            GraphPeriod.M1 -> DateUtils.nMonthsAgo(1)
            GraphPeriod.Y1 -> DateUtils.nYearsAgo(1)
        }
        return from to to
    }

    class Factory(
        private val weightRepo: WeightRepository,
        private val bodyFatRepo: BodyFatRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            GraphViewModel(weightRepo, bodyFatRepo) as T
    }
}
