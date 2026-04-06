package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseRecordDao {

    @Query("SELECT * FROM exercise_record WHERE date = :date")
    fun getByDate(date: String): Flow<List<ExerciseRecordEntity>>

    @Insert
    suspend fun insert(entity: ExerciseRecordEntity)

    @Update
    suspend fun update(entity: ExerciseRecordEntity)

    @Delete
    suspend fun delete(entity: ExerciseRecordEntity)
}