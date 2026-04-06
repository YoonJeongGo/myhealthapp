package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.MealItemEntity

@Dao
interface MealItemDao {

    @Insert
    suspend fun insertAll(items: List<MealItemEntity>)

    @Insert
    suspend fun insert(item: MealItemEntity)

    @Update
    suspend fun update(item: MealItemEntity)

    @Delete
    suspend fun delete(item: MealItemEntity)

    @Query("SELECT COALESCE(SUM(calories), 0) FROM meal_item WHERE meal_record_id = :recordId")
    suspend fun sumCaloriesByRecord(recordId: Int): Int
}