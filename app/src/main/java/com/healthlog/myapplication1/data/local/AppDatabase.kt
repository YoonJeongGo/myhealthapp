package com.healthlog.myapplication1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.healthlog.myapplication1.data.local.dao.BodyFatLogDao
import com.healthlog.myapplication1.data.local.dao.ExerciseRecordDao
import com.healthlog.myapplication1.data.local.dao.MealItemDao
import com.healthlog.myapplication1.data.local.dao.MealRecordDao
import com.healthlog.myapplication1.data.local.dao.WeightLogDao
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import com.healthlog.myapplication1.data.local.entity.MealItemEntity
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity

@Database(
    entities = [
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

    abstract fun weightDao(): WeightLogDao
    abstract fun bodyFatDao(): BodyFatLogDao
    abstract fun mealRecordDao(): MealRecordDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun exerciseRecordDao(): ExerciseRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}