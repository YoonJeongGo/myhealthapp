package com.healthlog.myapplication1.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_record",
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
data class ExerciseRecordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,
    val type: String, // 예: "헬스", "런닝"
    val durationMinutes: Int,

    val caloriesBurned: Int? = null,

    val createdAt: Long = System.currentTimeMillis()
)