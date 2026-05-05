package com.healthlog.myapplication1.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.repository.DailyLogRepository
import com.healthlog.myapplication1.domain.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CalendarViewModel(private val repo: DailyLogRepository) : ViewModel() {

    private val _yearMonth = MutableStateFlow(DateUtils.currentYearMonth())
    val yearMonth: StateFlow<String> = _yearMonth.asStateFlow()

    private val _logs = MutableStateFlow<Map<String, DailyLogEntity>>(emptyMap())
    val logs: StateFlow<Map<String, DailyLogEntity>> = _logs.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.today())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedLog = MutableStateFlow<DailyLogEntity?>(null)
    val selectedLog: StateFlow<DailyLogEntity?> = _selectedLog.asStateFlow()

    init { loadMonth(_yearMonth.value) }

    fun prevMonth() {
        _yearMonth.value = DateUtils.prevMonth(_yearMonth.value)
        loadMonth(_yearMonth.value)
    }

    fun nextMonth() {
        _yearMonth.value = DateUtils.nextMonth(_yearMonth.value)
        loadMonth(_yearMonth.value)
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        _selectedLog.value = _logs.value[date]
    }

    private fun loadMonth(ym: String) {
        viewModelScope.launch {
            val (from, to) = DateUtils.monthRange(ym)
            repo.getRange(from, to).collect { list ->
                _logs.value = list.associateBy { it.date }
                _selectedLog.value = _logs.value[_selectedDate.value]
            }
        }
    }

    class Factory(private val repo: DailyLogRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = CalendarViewModel(repo) as T
    }
}
