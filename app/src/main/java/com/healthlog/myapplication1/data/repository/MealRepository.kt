package com.healthlog.myapplication1.data.repository

import androidx.room.withTransaction
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.local.entity.MealItemEntity
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity

class MealRepository(
    private val db: AppDatabase
) {

    private val dailyLogDao = db.dailyLogDao()
    private val mealRecordDao = db.mealRecordDao()
    private val mealItemDao = db.mealItemDao()

    /**
     * 한 끼 저장
     *
     * 현재 프로젝트 실제 구조 기준:
     * - MealRecordEntity: id, date, mealType, totalCalories
     * - MealItemEntity: mealRecordId, name, calories, amount, isEstimated, sortOrder
     *
     * 동작:
     * 1) daily_log 없으면 생성
     * 2) meal_record 먼저 생성
     * 3) meal_item 여러 개 저장
     * 4) meal_item 합산 칼로리를 meal_record.totalCalories에 반영
     */
    suspend fun saveMeal(
        date: String,
        mealType: String,
        items: List<MealItemInput>,
        now: Long = System.currentTimeMillis()
    ): Long {
        require(date.isNotBlank()) { "date must not be blank" }
        require(mealType.isNotBlank()) { "mealType must not be blank" }
        require(items.isNotEmpty()) { "items must not be empty" }
        require(items.all { it.name.isNotBlank() }) { "item name must not be blank" }
        require(items.all { it.calories >= 0 }) { "item calories must not be negative" }

        return db.withTransaction {
            ensureDailyLogExists(date = date, now = now)

            val recordId = mealRecordDao.insert(
                MealRecordEntity(
                    date = date,
                    mealType = mealType,
                    totalCalories = 0
                )
            ).toInt()

            val mealItems = items.mapIndexed { index, item ->
                MealItemEntity(
                    mealRecordId = recordId,
                    name = item.name,
                    calories = item.calories,
                    amount = item.amount,
                    isEstimated = item.isEstimated,
                    sortOrder = index
                )
            }

            mealItemDao.insertAll(mealItems)

            val totalCalories = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(
                id = recordId,
                calories = totalCalories
            )

            recordId.toLong()
        }
    }

    /**
     * 특정 날짜의 meal_record 목록 조회
     * 현재 DAO 구조상 MealItem까지 JOIN 조회는 없음
     */
    suspend fun getMealsByDate(date: String): List<MealRecordEntity> {
        require(date.isNotBlank()) { "date must not be blank" }
        return mealRecordDao.getByDate(date)
    }

    /**
     * meal_record 단건 수정
     * 현재 구조상 mealType / totalCalories 정도만 수정 가능
     */
    suspend fun updateMealRecord(entity: MealRecordEntity) {
        require(entity.date.isNotBlank()) { "date must not be blank" }
        require(entity.mealType.isNotBlank()) { "mealType must not be blank" }
        require(entity.totalCalories >= 0) { "totalCalories must not be negative" }

        db.withTransaction {
            mealRecordDao.update(entity)
        }
    }

    /**
     * meal_item 1건 추가 후 totalCalories 재계산
     */
    suspend fun addMealItem(
        recordId: Int,
        item: MealItemInput
    ) {
        require(recordId > 0) { "recordId must be greater than 0" }
        require(item.name.isNotBlank()) { "name must not be blank" }
        require(item.calories >= 0) { "calories must not be negative" }

        db.withTransaction {
            mealItemDao.insert(
                MealItemEntity(
                    mealRecordId = recordId,
                    name = item.name,
                    calories = item.calories,
                    amount = item.amount,
                    isEstimated = item.isEstimated,
                    sortOrder = 0
                )
            )

            val totalCalories = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(
                id = recordId,
                calories = totalCalories
            )
        }
    }

    /**
     * meal_item 수정 후 totalCalories 재계산
     */
    suspend fun updateMealItem(
        recordId: Int,
        item: MealItemEntity
    ) {
        require(recordId > 0) { "recordId must be greater than 0" }
        require(item.name.isNotBlank()) { "name must not be blank" }
        require(item.calories >= 0) { "calories must not be negative" }

        db.withTransaction {
            mealItemDao.update(item)

            val totalCalories = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(
                id = recordId,
                calories = totalCalories
            )
        }
    }

    /**
     * meal_item 삭제 후 totalCalories 재계산
     */
    suspend fun deleteMealItem(
        recordId: Int,
        item: MealItemEntity
    ) {
        require(recordId > 0) { "recordId must be greater than 0" }

        db.withTransaction {
            mealItemDao.delete(item)

            val totalCalories = mealItemDao.sumCaloriesByRecord(recordId)
            mealRecordDao.updateTotalCalories(
                id = recordId,
                calories = totalCalories
            )
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