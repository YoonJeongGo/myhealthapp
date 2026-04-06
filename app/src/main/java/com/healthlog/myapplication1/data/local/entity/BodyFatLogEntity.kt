package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "body_fat_log",
    indices = [Index(value = ["date"])]
)
data class BodyFatLogEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String, // "2025-01-15"

    @ColumnInfo(name = "body_fat")
    val bodyFat: Float, // %

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "note")
    val note: String? = null
)