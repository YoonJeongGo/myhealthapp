package com.healthlog.myapplication1.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_record",
    foreignKeys = [
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["date"],
            childColumns = ["date"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["date"])]
)
data class MealRecordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,
    val mealType: String, // BREAKFAST, LUNCH, DINNER, SNACK
    val rawInput: String,

    val totalCalories: Int,
    val isAiEstimated: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)