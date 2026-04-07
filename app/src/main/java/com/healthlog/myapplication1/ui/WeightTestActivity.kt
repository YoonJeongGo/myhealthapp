package com.healthlog.myapplication1.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.healthlog.myapplication1.R
import com.healthlog.myapplication1.data.local.AppDatabase
import com.healthlog.myapplication1.data.repository.WeightRepository
import com.healthlog.myapplication1.ui.viewmodel.WeightViewModel
import kotlinx.coroutines.launch

class WeightTestActivity : AppCompatActivity() {

    private lateinit var viewModel: WeightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_test)

        val db = AppDatabase.getDatabase(this)
        val repository = WeightRepository(db)
        viewModel = WeightViewModel(repository)

        val editDate = findViewById<EditText>(R.id.editDate)
        val editWeight = findViewById<EditText>(R.id.editWeight)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val textResult = findViewById<TextView>(R.id.textResult)

        btnSave.setOnClickListener {

            val date = editDate.text.toString()
            val weightText = editWeight.text.toString()

            if (date.isBlank() || weightText.isBlank()) {
                textResult.text = "값 입력해라"
                return@setOnClickListener
            }

            val weight = weightText.toFloatOrNull()
            if (weight == null) {
                textResult.text = "숫자 입력해라"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    viewModel.saveWeight(date, weight)
                    textResult.text = "저장 성공"

                    val latest = viewModel.latestWeight.value
                    textResult.text = "저장됨: ${latest?.weight}"

                } catch (e: Exception) {
                    textResult.text = "에러: ${e.message}"
                }
            }
        }
    }
}