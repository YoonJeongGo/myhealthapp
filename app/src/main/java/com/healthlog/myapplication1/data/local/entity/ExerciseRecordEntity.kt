package com.healthlog.myapplication1.data.repository

import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val db: AppDatabase
) {

    private val exerciseRecordDao = db.exerciseRecordDao()

    suspend fun insertExercise(
        date: String,
        exerciseName: String,
        duration: Int,
        calories: Int,
        note: String? = null
    ) {
        require(date.isNotBlank()) { "date must not be blank" }
        require(exerciseName.isNotBlank()) { "exerciseName must not be blank" }
        require(duration >= 0) { "duration must not be negative" }
        require(calories >= 0) { "calories must not be negative" }

        exerciseRecordDao.insert(
            ExerciseRecordEntity(
                date = date,
                exerciseName = exerciseName,
                duration = duration,
                calories = calories,
                note = note
            )
        )
    }

    fun getExercisesByDate(date: String): Flow<List<ExerciseRecordEntity>> {
        require(date.isNotBlank()) { "date must not be blank" }
        return exerciseRecordDao.getByDate(date)
    }

    suspend fun updateExercise(entity: ExerciseRecordEntity) {
        require(entity.date.isNotBlank()) { "date must not be blank" }
        require(entity.exerciseName.isNotBlank()) { "exerciseName must not be blank" }
        require(entity.duration >= 0) { "duration must not be negative" }
        require(entity.calories >= 0) { "calories must not be negative" }

        exerciseRecordDao.update(entity)
    }

    suspend fun deleteExercise(entity: ExerciseRecordEntity) {
        exerciseRecordDao.delete(entity)
    }
}