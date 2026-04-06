package com.healthlog.myapplication1.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meal_item",
    foreignKeys = [
        ForeignKey(
            entity = MealRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["meal_record_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["meal_record_id"])]
)
data class MealItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "meal_record_id")
    val mealRecordId: Int,

    val name: String,
    val calories: Int,
    val amount: String? = null,

    @ColumnInfo(name = "is_estimated")
    val isEstimated: Boolean = true,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)