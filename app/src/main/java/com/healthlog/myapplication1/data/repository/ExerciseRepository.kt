package com.healthlog.myapplication1.data.repository

import com.healthlog.myapplication1.data.local.dao.ExerciseRecordDao
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity

class ExerciseRepository(
    private val exerciseRecordDao: ExerciseRecordDao
) {

    suspend fun insertExercise(
        date: String,
        exerciseName: String,
        duration: Int,
        calories: Int,
        note: String?
    ) {
        val entity = ExerciseRecordEntity(
            date = date,
            exerciseName = exerciseName,
            duration = duration,
            calories = calories,
            note = note
        )

        exerciseRecordDao.insert(entity)
    }

    suspend fun getExercisesByDate(date: String): List<ExerciseRecordEntity> {
        return exerciseRecordDao.getByDate(date)
    }
}