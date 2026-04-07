package com.healthlog.myapplication1.data.repository

import androidx.room.withTransaction
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val db: AppDatabase
) {

    private val dailyLogDao = db.dailyLogDao()
    private val exerciseRecordDao = db.exerciseRecordDao()

    suspend fun insertExercise(
        date: String,
        type: String,
        durationMinutes: Int,
        caloriesBurned: Int,
        createdAt: Long = System.currentTimeMillis()
    ) {
        require(date.isNotBlank()) { "date must not be blank" }
        require(type.isNotBlank()) { "type must not be blank" }
        require(durationMinutes >= 0) { "durationMinutes must not be negative" }
        require(caloriesBurned >= 0) { "caloriesBurned must not be negative" }

        db.withTransaction {
            val daily = ensureDailyLogExists(date = date, now = createdAt)

            exerciseRecordDao.insert(
                ExerciseRecordEntity(
                    date = date,
                    type = type,
                    durationMinutes = durationMinutes,
                    caloriesBurned = caloriesBurned,
                    createdAt = createdAt
                )
            )

            if (!daily.hasExercise) {
                dailyLogDao.update(
                    daily.copy(hasExercise = true)
                )
            }
        }
    }

    fun getExercisesByDate(date: String): Flow<List<ExerciseRecordEntity>> {
        require(date.isNotBlank()) { "date must not be blank" }
        return exerciseRecordDao.getByDate(date)
    }

    suspend fun updateExercise(entity: ExerciseRecordEntity) {
        require(entity.date.isNotBlank()) { "date must not be blank" }
        require(entity.type.isNotBlank()) { "type must not be blank" }

        db.withTransaction {
            exerciseRecordDao.update(entity)
        }
    }

    suspend fun deleteExercise(entity: ExerciseRecordEntity) {
        db.withTransaction {
            exerciseRecordDao.delete(entity)
        }
    }

    private suspend fun ensureDailyLogExists(
        date: String,
        now: Long
    ): DailyLogEntity {
        val existing = dailyLogDao.getByDate(date)
        if (existing != null) {
            return existing
        }

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

        return dailyLogDao.getByDate(date)
            ?: throw IllegalStateException("daily_log creation failed for date=$date")
    }
}