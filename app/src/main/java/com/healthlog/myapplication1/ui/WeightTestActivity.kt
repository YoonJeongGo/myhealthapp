package com.healthlog.myapplication1.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.healthlog.myapplication1.R
import com.healthlog.myapplication1.HealthLogApplication
import com.healthlog.myapplication1.ui.viewmodel.WeightViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeightTestActivity : Activity() {

    private lateinit var viewModel: WeightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_test)

        val app = applicationContext as HealthLogApplication
        viewModel = WeightViewModel(app.container.weightRepository)

        val editDate   = findViewById<EditText>(R.id.editDate)
        val editWeight = findViewById<EditText>(R.id.editWeight)
        val btnSave    = findViewById<Button>(R.id.btnSave)
        val textResult = findViewById<TextView>(R.id.textResult)

        btnSave.setOnClickListener {
            val date   = editDate.text.toString().trim()
            val weight = editWeight.text.toString().trim().toFloatOrNull()

            if (date.isBlank() || weight == null) {
                textResult.text = "날짜와 몸무게를 입력하세요"
                return@setOnClickListener
            }

            textResult.text = "저장 중..."
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) { viewModel.saveWeight(date, weight) }
                textResult.text = "저장 성공: $weight kg"
            }
        }
    }
}
