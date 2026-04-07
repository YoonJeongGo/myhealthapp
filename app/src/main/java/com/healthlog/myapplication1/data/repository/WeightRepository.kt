package com.healthlog.myapplication1.data.repository

import androidx.room.withTransaction
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

class WeightRepository(
    private val db: AppDatabase
) {

    private val dailyLogDao = db.dailyLogDao()
    private val weightLogDao = db.weightLogDao()

    suspend fun insertWeight(
        date: String,
        weight: Float,
        timestamp: Long = System.currentTimeMillis(),
        note: String? = null
    ): Long {
        require(date.isNotBlank()) { "date must not be blank" }
        require(weight > 0f) { "weight must be greater than 0" }

        return db.withTransaction {
            ensureDailyLogExists(date = date, now = timestamp)

            val insertedId = weightLogDao.insert(
                WeightLogEntity(
                    date = date,
                    weight = weight,
                    timestamp = timestamp,
                    note = note
                )
            )

            dailyLogDao.updateLatestWeight(date, weight)
            insertedId
        }
    }

    fun getWeightsByDate(date: String): Flow<List<WeightLogEntity>> {
        require(date.isNotBlank()) { "date must not be blank" }
        return weightLogDao.getByDate(date)
    }

    fun getLatestPerDayInRange(from: String, to: String): Flow<List<WeightLogEntity>> {
        require(from.isNotBlank()) { "from must not be blank" }
        require(to.isNotBlank()) { "to must not be blank" }
        return weightLogDao.getLatestPerDayInRange(from, to)
    }

    suspend fun getLatestWeight(): WeightLogEntity? {
        return weightLogDao.getLatest()
    }

    suspend fun updateWeight(entity: WeightLogEntity) {
        require(entity.date.isNotBlank()) { "date must not be blank" }
        require(entity.weight > 0f) { "weight must be greater than 0" }

        db.withTransaction {
            ensureDailyLogExists(date = entity.date, now = entity.timestamp)
            weightLogDao.update(entity)
            dailyLogDao.updateLatestWeight(entity.date, entity.weight)
        }
    }

    suspend fun deleteWeight(entity: WeightLogEntity) {
        db.withTransaction {
            weightLogDao.delete(entity)
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