package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity
import com.healthlog.myapplication1.data.local.entity.MealRecordWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface MealRecordDao {

    @Transaction
    @Query("SELECT * FROM meal_record WHERE date = :date")
    fun getWithItemsByDate(date: String): Flow<List<MealRecordWithItems>>

    @Transaction
    @Query("SELECT * FROM meal_record WHERE id = :id LIMIT 1")
    suspend fun getWithItemsById(id: Int): MealRecordWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(entity: MealRecordEntity): Long

    @Query("UPDATE meal_record SET total_calories = :calories, updated_at = :now WHERE id = :id")
    suspend fun updateTotalCalories(id: Int, calories: Int, now: Long = System.currentTimeMillis())

    @Update
    suspend fun updateRecord(entity: MealRecordEntity)

    @Delete
    suspend fun deleteRecord(entity: MealRecordEntity)
}