package com.healthlog.myapplication1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(

    @PrimaryKey
    val id: Int = 0,

    val name: String,
    val age: Int,
    val height: Float,
    val weight: Float,

    val createdAt: Long = System.currentTimeMillis()
)