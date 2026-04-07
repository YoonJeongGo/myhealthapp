package com.healthlog.myapplication1.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MealRecordWithItems(

    @Embedded
    val record: MealRecordEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "meal_record_id"
    )
    val items: List<MealItemEntity>
)