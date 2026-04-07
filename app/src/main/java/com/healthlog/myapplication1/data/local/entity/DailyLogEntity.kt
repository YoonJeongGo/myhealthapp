package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_log",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class DailyLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String, // "2025-01-15"

    @ColumnInfo(name = "latest_weight")
    val latestWeight: Float? = null,

    @ColumnInfo(name = "latest_body_fat")
    val latestBodyFat: Float? = null,

    @ColumnInfo(name = "total_calories")
    val totalCalories: Int = 0,

    @ColumnInfo(name = "has_exercise")
    val hasExercise: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)