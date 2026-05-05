package com.healthlog.myapplication1.domain.util

object BmrCalculator {

    // Harris-Benedict 공식
    fun calculateBmr(gender: String, weight: Float, height: Float, age: Int): Float {
        return if (gender == "MALE") {
            88.362f + (13.397f * weight) + (4.799f * height) - (5.677f * age)
        } else {
            447.593f + (9.247f * weight) + (3.098f * height) - (4.330f * age)
        }
    }

    // 활동계수: 1.2(거의 안 움직임) ~ 1.9(매우 활동적)
    fun calculateTdee(bmr: Float, activityLevel: Float): Float = bmr * activityLevel

    fun activityLabel(level: Float): String = when {
        level <= 1.2f  -> "거의 안 움직임"
        level <= 1.375f -> "가벼운 운동 (주 1~3회)"
        level <= 1.55f  -> "보통 운동 (주 3~5회)"
        level <= 1.725f -> "활동적 (주 6~7회)"
        else            -> "매우 활동적 (육체 노동)"
    }

    fun goalLabel(goal: String): String = when (goal) {
        "LOSS"     -> "체중 감량"
        "MAINTAIN" -> "체중 유지"
        "GAIN"     -> "근육 증량"
        else       -> goal
    }

    fun targetCalories(tdee: Float, goal: String): Float = when (goal) {
        "LOSS"  -> tdee - 500f
        "GAIN"  -> tdee + 300f
        else    -> tdee
    }
}
