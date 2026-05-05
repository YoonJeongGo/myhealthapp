package com.healthlog.myapplication1

import android.app.Application
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.repository.*

class AppContainer(db: AppDatabase) {
    val weightRepository    = WeightRepository(db)
    val bodyFatRepository   = BodyFatRepository(db)
    val mealRepository      = MealRepository(db)
    val exerciseRepository  = ExerciseRepository(db.exerciseRecordDao())
    val userRepository      = UserRepository(db.userProfileDao())
    val dailyLogRepository  = DailyLogRepository(db.dailyLogDao())

    // OpenAI API 키를 여기에 입력하세요 (비워두면 AI 기능 비활성화)
    val aiRepository        = AiRepository(apiKey = "")
}

class HealthLogApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(AppDatabase.getDatabase(this))
    }
}
