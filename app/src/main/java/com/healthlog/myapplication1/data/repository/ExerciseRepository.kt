package com.healthlog.myapplication1.data.repository

import com.healthlog.myapplication1.data.local.dao.ExerciseRecordDao
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val dao: ExerciseRecordDao) {

    suspend fun insertExercise(
        date: String,
        exerciseName: String,
        duration: Int,
        calories: Int,
        note: String? = null
    ) {
        dao.insert(
            ExerciseRecordEntity(
                date = date,
                exerciseName = exerciseName,
                duration = duration,
                calories = calories,
                note = note
            )
        )
    }

    fun getExercisesByDate(date: String): Flow<List<ExerciseRecordEntity>> = dao.getByDate(date)

    suspend fun deleteExercise(entity: ExerciseRecordEntity) = dao.delete(entity)

    suspend fun updateExercise(entity: ExerciseRecordEntity) = dao.update(entity)
}
