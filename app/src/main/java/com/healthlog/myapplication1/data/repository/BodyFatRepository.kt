package com.healthlog.myapplication1.data.repository

import androidx.room.withTransaction
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

class BodyFatRepository(
    private val db: AppDatabase
) {

    private val dailyLogDao = db.dailyLogDao()
    private val bodyFatLogDao = db.bodyFatLogDao()

    suspend fun insertBodyFat(
        date: String,
        bodyFat: Float,
        timestamp: Long = System.currentTimeMillis(),
        note: String? = null
    ): Long {
        require(date.isNotBlank()) { "date must not be blank" }
        require(bodyFat > 0f) { "bodyFat must be greater than 0" }

        return db.withTransaction {
            ensureDailyLogExists(date = date, now = timestamp)

            val insertedId = bodyFatLogDao.insert(
                BodyFatLogEntity(
                    date = date,
                    bodyFat = bodyFat,
                    timestamp = timestamp,
                    note = note
                )
            )

            dailyLogDao.updateLatestBodyFat(date, bodyFat)
            insertedId
        }
    }

    fun getBodyFatByDate(date: String): Flow<List<BodyFatLogEntity>> {
        require(date.isNotBlank()) { "date must not be blank" }
        return bodyFatLogDao.getByDate(date)
    }

    fun getLatestPerDayInRange(from: String, to: String): Flow<List<BodyFatLogEntity>> {
        require(from.isNotBlank()) { "from must not be blank" }
        require(to.isNotBlank()) { "to must not be blank" }
        return bodyFatLogDao.getLatestPerDayInRange(from, to)
    }

    suspend fun getLatestBodyFat(): BodyFatLogEntity? {
        return bodyFatLogDao.getLatest()
    }

    suspend fun updateBodyFat(entity: BodyFatLogEntity) {
        require(entity.date.isNotBlank()) { "date must not be blank" }
        require(entity.bodyFat > 0f) { "bodyFat must be greater than 0" }

        db.withTransaction {
            ensureDailyLogExists(date = entity.date, now = entity.timestamp)
            bodyFatLogDao.update(entity)
            dailyLogDao.updateLatestBodyFat(entity.date, entity.bodyFat)
        }
    }

    suspend fun deleteBodyFat(entity: BodyFatLogEntity) {
        db.withTransaction {
            bodyFatLogDao.delete(entity)
        }
    }

    private suspend fun ensureDailyLogExists(
        date: String,
        now: Long
    ) {
        val existing = dailyLogDao.getByDate(date)
        if (existing == null) {
            dailyLogDao.insertIfAbsent(
                DailyLogEntity(
                    date = date,
                    latestWeight = null,
                    latestBodyFat = null,
                    totalCalories = 0,
                    hasExercise = false,
                    createdAt = now
                )
            )
        }
    }
}