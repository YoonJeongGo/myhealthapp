package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_record",
    indices = [Index(value = ["date"])]
)
data class ExerciseRecordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String, // 예: "2026-04-07"

    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,

    @ColumnInfo(name = "duration")
    val duration: Int, // 분

    @ColumnInfo(name = "calories")
    val calories: Int, // kcal

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "note")
    val note: String? = null
)