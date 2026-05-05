package com.healthlog.myapplication1.data.repository

import androidx.room.withTransaction
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.*
import com.healthlog.myapplication1.data.remote.dto.ParsedMealItem
import kotlinx.coroutines.flow.Flow

class MealRepository(private val db: AppDatabase) {

    private val dailyLogDao   = db.dailyLogDao()
    private val mealRecordDao = db.mealRecordDao()
    private val mealItemDao   = db.mealItemDao()

    fun getMealsByDate(date: String): Flow<List<MealRecordWithItems>> =
        mealRecordDao.getWithItemsByDate(date)

    suspend fun saveMealWithItems(
        date: String,
        mealType: String,
        rawInput: String = "",
        items: List<MealItemInput>,
        isAiEstimated: Boolean = false,
        now: Long = System.currentTimeMillis()
    ): Long {
        require(date.isNotBlank())
        require(mealType.isNotBlank())
        require(items.isNotEmpty())

        return db.withTransaction {
            ensureDailyLog(date, now)

            val recordId = mealRecordDao.insert(
                MealRecordEntity(
                    date = date,
                    mealType = mealType,
                    rawInput = rawInput,
                    totalCalories = 0,
                    isAiEstimated = isAiEstimated
                )
            ).toInt()

            mealItemDao.insertAll(items.mapIndexed { idx, item ->
                MealItemEntity(
                    mealRecordId = recordId,
                    name = item.name,
                    calories = item.calories,
                    amount = item.amount,
                    isEstimated = item.isEstimated,
                    sortOrder = idx
                )
            })

            val total = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(recordId, total)
            recordId.toLong()
        }
    }

    suspend fun saveParsedMeal(
        date: String,
        mealType: String,
        rawInput: String,
        parsedItems: List<ParsedMealItem>
    ): Long = saveMealWithItems(
        date = date,
        mealType = mealType,
        rawInput = rawInput,
        items = parsedItems.map {
            MealItemInput(it.name, it.calories, it.unit, it.isEstimated)
        },
        isAiEstimated = true
    )

    suspend fun deleteMealRecord(entity: MealRecordEntity) {
        mealRecordDao.delete(entity)
    }

    suspend fun addMealItem(recordId: Int, item: MealItemInput) {
        db.withTransaction {
            mealItemDao.insert(
                MealItemEntity(
                    mealRecordId = recordId,
                    name = item.name,
                    calories = item.calories,
                    amount = item.amount,
                    isEstimated = item.isEstimated
                )
            )
            val total = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(recordId, total)
        }
    }

    suspend fun deleteMealItem(recordId: Int, item: MealItemEntity) {
        db.withTransaction {
            mealItemDao.delete(item)
            val total = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(recordId, total)
        }
    }

    private suspend fun ensureDailyLog(date: String, now: Long) {
        if (dailyLogDao.getByDate(date) == null) {
            dailyLogDao.insertIfAbsent(DailyLogEntity(date = date, createdAt = now))
        }
    }
}
