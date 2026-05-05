package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(

    @PrimaryKey
    val id: Int = 0,

    val name: String,
    val age: Int,
    val gender: String,         // "MALE" or "FEMALE"
    val height: Float,          // cm
    val weight: Float,          // kg (initial)
    val goal: String,           // "LOSS" / "MAINTAIN" / "GAIN"

    @ColumnInfo(name = "activity_level")
    val activityLevel: Float,   // 1.2 ~ 1.9

    val bmr: Float,
    val tdee: Float,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
