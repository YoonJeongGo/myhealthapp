package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_log ORDER BY date DESC LIMIT 30")
    fun getRecent30(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_log WHERE date BETWEEN :from AND :to")
    fun getRange(from: String, to: String): Flow<List<DailyLogEntity>>

    @Query("UPDATE daily_log SET latest_weight = :weight WHERE date = :date")
    suspend fun updateLatestWeight(date: String, weight: Float)

    @Query("UPDATE daily_log SET latest_body_fat = :bodyFat WHERE date = :date")
    suspend fun updateLatestBodyFat(date: String, bodyFat: Float)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(entity: DailyLogEntity)

    @Update
    suspend fun update(entity: DailyLogEntity)

    @Delete
    suspend fun delete(entity: DailyLogEntity)
}