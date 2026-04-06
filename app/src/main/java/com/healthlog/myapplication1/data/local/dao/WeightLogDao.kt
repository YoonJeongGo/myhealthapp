package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {

    @Query("SELECT * FROM weight_log WHERE date = :date ORDER BY timestamp DESC")
    fun getByDate(date: String): Flow<List<WeightLogEntity>>

    @Query("""
        SELECT w1.* FROM weight_log w1
        INNER JOIN (
            SELECT date, MAX(timestamp) AS maxTs
            FROM weight_log
            WHERE date BETWEEN :from AND :to
            GROUP BY date
        ) w2 ON w1.date = w2.date AND w1.timestamp = w2.maxTs
        ORDER BY w1.date ASC
    """)
    fun getLatestPerDayInRange(from: String, to: String): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_log ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): WeightLogEntity?

    @Insert
    suspend fun insert(entity: WeightLogEntity): Long

    @Update
    suspend fun update(entity: WeightLogEntity)

    @Delete
    suspend fun delete(entity: WeightLogEntity)
}