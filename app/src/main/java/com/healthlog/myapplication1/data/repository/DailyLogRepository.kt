package com.healthlog.myapplication1.data.repository

import com.healthlog.myapplication1.data.local.dao.DailyLogDao
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

class DailyLogRepository(private val dao: DailyLogDao) {

    suspend fun getOrCreate(date: String): DailyLogEntity {
        val existing = dao.getByDate(date)
        if (existing != null) return existing
        dao.insertIfAbsent(DailyLogEntity(date = date))
        return dao.getByDate(date)!!
    }

    suspend fun getByDate(date: String): DailyLogEntity? = dao.getByDate(date)

    fun getRecent30(): Flow<List<DailyLogEntity>> = dao.getRecent30()

    fun getRange(from: String, to: String): Flow<List<DailyLogEntity>> = dao.getRange(from, to)

    suspend fun updateTotalCalories(date: String, totalCalories: Int) {
        val log = dao.getByDate(date) ?: return
        dao.update(log.copy(totalCalories = totalCalories))
    }

    suspend fun setHasExercise(date: String, has: Boolean) {
        val log = dao.getByDate(date) ?: dao.insertIfAbsent(DailyLogEntity(date = date)).let { dao.getByDate(date) } ?: return
        dao.update(log.copy(hasExercise = has))
    }
}
