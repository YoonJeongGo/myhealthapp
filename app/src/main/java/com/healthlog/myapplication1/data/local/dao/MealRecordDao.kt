package com.healthlog.myapplication1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity

@Dao
interface MealRecordDao {

    @Insert
    suspend fun insert(record: MealRecordEntity): Long

    @Update
    suspend fun update(record: MealRecordEntity)

    @Query("SELECT * FROM meal_record WHERE date = :date")
    suspend fun getByDate(date: String): List<MealRecordEntity>

    @Query("""
        UPDATE meal_record 
        SET totalCalories = :calories 
        WHERE id = :id
    """)
    suspend fun updateTotalCalories(
        id: Int,
        calories: Int
    )
}