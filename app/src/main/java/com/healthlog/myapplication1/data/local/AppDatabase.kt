package com.healthlog.myapplication1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.healthlog.myapplication1.data.local.dao.BodyFatLogDao
import com.healthlog.myapplication1.data.local.dao.DailyLogDao
import com.healthlog.myapplication1.data.local.dao.ExerciseRecordDao
import com.healthlog.myapplication1.data.local.dao.MealItemDao
import com.healthlog.myapplication1.data.local.dao.MealRecordDao
import com.healthlog.myapplication1.data.local.dao.UserProfileDao
import com.healthlog.myapplication1.data.local.dao.WeightLogDao
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import com.healthlog.myapplication1.data.local.entity.MealItemEntity
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity
import com.healthlog.myapplication1.data.local.entity.UserProfileEntity
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity

@Database(
    entities = [
        UserProfileEntity::class,
        DailyLogEntity::class,
        WeightLogEntity::class,
        BodyFatLogEntity::class,
        MealRecordEntity::class,
        MealItemEntity::class,
        ExerciseRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun bodyFatLogDao(): BodyFatLogDao
    abstract fun mealRecordDao(): MealRecordDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun exerciseRecordDao(): ExerciseRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthlog.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}