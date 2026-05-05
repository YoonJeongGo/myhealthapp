package com.healthlog.myapplication1.ui.screen.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.components.NumericKeypad
import com.healthlog.myapplication1.ui.theme.*

@Composable
fun WeightInputSheet(
    viewModel: WeightInputViewModel,
    date: String,
    onDone: () -> Unit
) {
    val saveState by viewModel.saveState.collectAsState()
    val previous  by viewModel.previousWeight.collectAsState()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) { viewModel.reset(); onDone() }
    }

    val weight = input.toFloatOrNull()
    val delta  = if (weight != null && previous != null) weight - previous!! else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 핸들
        Box(Modifier.width(40.dp).height(4.dp).align(Alignment.CenterHorizontally)
            .then(Modifier.padding(bottom = 16.dp))) {
            Surface(shape = RoundedCornerShape(2.dp), color = Border2) { Box(Modifier.fillMaxSize()) }
        }
        Spacer(Modifier.height(8.dp))

        Text("몸무게 기록", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(DateUtils.toDisplayShort(date), fontSize = 12.sp, color = TextTertiary)

        Spacer(Modifier.height(24.dp))

        // 큰 숫자 표시
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = if (input.isEmpty()) "0.0" else input,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = if (input.isEmpty()) TextTertiary else GreenAccent
            )
            Text(" kg", fontSize = 20.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
        }

        if (previous != null) {
            Text("이전: ${"%.1f".format(previous)} kg", fontSize = 11.sp, color = TextTertiary)
        }
        if (delta != null) {
            val sign = if (delta >= 0) "+" else ""
            Text(
                text = "$sign${"%.1f".format(delta)} kg",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (delta < 0) GreenAccent else Warning,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        NumericKeypad(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val w = input.toFloatOrNull() ?: return@Button
                viewModel.save(date, w)
            },
            enabled = weight != null && weight > 0f && saveState !is SaveState.Loading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = BgBase)
        ) {
            if (saveState is SaveState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BgBase, strokeWidth = 2.dp)
            } else {
                Text("저장", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
