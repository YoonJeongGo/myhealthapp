package com.healthlog.myapplication1.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthlog.myapplication1.data.local.entity.UserProfileEntity
import com.healthlog.myapplication1.data.repository.UserRepository
import com.healthlog.myapplication1.domain.util.BmrCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repo: UserRepository) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _existing = MutableStateFlow<UserProfileEntity?>(null)
    val existing: StateFlow<UserProfileEntity?> = _existing.asStateFlow()

    init {
        viewModelScope.launch { _existing.value = repo.getProfile() }
    }

    fun save(
        name: String,
        age: Int,
        gender: String,
        height: Float,
        weight: Float,
        goal: String,
        activityLevel: Float
    ) {
        viewModelScope.launch {
            val bmr  = BmrCalculator.calculateBmr(gender, weight, height, age)
            val tdee = BmrCalculator.calculateTdee(bmr, activityLevel)
            repo.saveProfile(
                UserProfileEntity(
                    name          = name,
                    age           = age,
                    gender        = gender,
                    height        = height,
                    weight        = weight,
                    goal          = goal,
                    activityLevel = activityLevel,
                    bmr           = bmr,
                    tdee          = tdee
                )
            )
            _saved.value = true
        }
    }

    class Factory(private val repo: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            OnboardingViewModel(repo) as T
    }
}
