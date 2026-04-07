package com.healthlog.myapplication1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_record")
data class MealRecordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    val mealType: String,

    val totalCalories: Int = 0
)