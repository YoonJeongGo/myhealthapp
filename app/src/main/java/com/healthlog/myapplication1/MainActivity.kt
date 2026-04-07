package com.healthlog.myapplication1

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.local.entity.BodyFatLogEntity
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.data.local.entity.ExerciseRecordEntity
import com.healthlog.myapplication1.data.local.entity.MealItemEntity
import com.healthlog.myapplication1.data.local.entity.MealRecordEntity
import com.healthlog.myapplication1.data.local.entity.WeightLogEntity
import kotlinx.coroutines.*

class MainActivity : Activity() {

    private lateinit var resultTextView: TextView
    private lateinit var runTestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val titleTextView = TextView(this).apply {
            text = "HealthLog DB Smoke Test"
            textSize = 22f
        }

        runTestButton = Button(this).apply {
            text = "DB Smoke Test 실행"
            setOnClickListener {
                runDatabaseSmokeTest()
            }
        }

        resultTextView = TextView(this).apply {
            text = "버튼 눌러 테스트"
            textSize = 16f
        }

        rootLayout.addView(titleTextView)
        rootLayout.addView(runTestButton)
        rootLayout.addView(resultTextView)

        val scrollView = ScrollView(this).apply {
            addView(rootLayout)
        }

        setContentView(scrollView)
    }

    private fun runDatabaseSmokeTest() {
        runTestButton.isEnabled = false
        resultTextView.text = "실행 중..."

        CoroutineScope(Dispatchers.Main).launch {

            val result = withContext(Dispatchers.IO) {

                val log = StringBuilder()

                try {
                    val db = AppDatabase.getInstance(applicationContext)

                    val dailyLogDao = db.dailyLogDao()
                    val weightLogDao = db.weightLogDao()
                    val bodyFatLogDao = db.bodyFatLogDao()   // ✔️ 수정됨
                    val mealRecordDao = db.mealRecordDao()
                    val mealItemDao = db.mealItemDao()
                    val exerciseRecordDao = db.exerciseRecordDao()

                    val date = "2026-04-07"
                    val now = System.currentTimeMillis()

                    val existing = dailyLogDao.getByDate(date)
                    if (existing == null) {
                        dailyLogDao.insertIfAbsent(
                            DailyLogEntity(
                                date = date,
                                createdAt = now
                            )
                        )
                    }

                    weightLogDao.insert(
                        WeightLogEntity(date = date, weight = 77f)
                    )

                    bodyFatLogDao.insert(
                        BodyFatLogEntity(date = date, bodyFat = 18f)
                    )

                    val mealId = mealRecordDao.insert(
                        MealRecordEntity(
                            date = date,
                            mealType = "LUNCH",
                            totalCalories = 0
                        )
                    ).toInt()

                    mealItemDao.insertAll(
                        listOf(
                            MealItemEntity(
                                mealRecordId = mealId,
                                name = "밥",
                                calories = 300
                            ),
                            MealItemEntity(
                                mealRecordId = mealId,
                                name = "닭가슴살",
                                calories = 150
                            )
                        )
                    )

                    val sum = mealItemDao.sumCaloriesByRecord(mealId)
                    mealRecordDao.updateTotalCalories(mealId, sum)

                    exerciseRecordDao.insert(
                        ExerciseRecordEntity(
                            date = date,
                            type = "헬스",
                            durationMinutes = 60,
                            caloriesBurned = 300,
                            createdAt = now
                        )
                    )

                    log.append("DB 테스트 성공\n")
                    log.append("meal total = $sum")

                } catch (e: Exception) {
                    log.append("실패: ${e.message}")
                }

                log.toString()
            }

            resultTextView.text = result
            runTestButton.isEnabled = true
        }
    }
}