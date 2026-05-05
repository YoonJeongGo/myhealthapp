package com.healthlog.myapplication1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.healthlog.myapplication1.data.local.dao.*
import com.healthlog.myapplication1.data.local.entity.*

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
    version = 3,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthlog.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
