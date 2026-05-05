package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_record",
    foreignKeys = [ForeignKey(
        entity = DailyLogEntity::class,
        parentColumns = ["date"],
        childColumns = ["date"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["date"])]
)
data class MealRecordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,                       // yyyy-MM-dd

    @ColumnInfo(name = "meal_type")
    val mealType: String,                   // BREAKFAST/LUNCH/DINNER/SNACK

    @ColumnInfo(name = "raw_input")
    val rawInput: String = "",              // 사용자 원문 입력

    @ColumnInfo(name = "total_calories")
    val totalCalories: Int = 0,             // meal_item.calories 합산 캐시

    @ColumnInfo(name = "is_ai_estimated")
    val isAiEstimated: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
