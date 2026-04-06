package com.healthlog.myapplication1.data.local.dao

import androidx.room.*
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyFatLogDao {

    @Query("SELECT * FROM body_fat_log WHERE date = :date ORDER BY timestamp DESC")
    fun getByDate(date: String): Flow<List<BodyFatLogEntity>>

    @Query("""
        SELECT b1.* FROM body_fat_log b1
        INNER JOIN (
            SELECT date, MAX(timestamp) AS maxTs
            FROM body_fat_log
            WHERE date BETWEEN :from AND :to
            GROUP BY date
        ) b2 ON b1.date = b2.date AND b1.timestamp = b2.maxTs
        ORDER BY b1.date ASC
    """)
    fun getLatestPerDayInRange(from: String, to: String): Flow<List<BodyFatLogEntity>>

    @Query("SELECT * FROM body_fat_log ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): BodyFatLogEntity?

    @Insert
    suspend fun insert(entity: BodyFatLogEntity): Long

    @Update
    suspend fun update(entity: BodyFatLogEntity)

    @Delete
    suspend fun delete(entity: BodyFatLogEntity)
}