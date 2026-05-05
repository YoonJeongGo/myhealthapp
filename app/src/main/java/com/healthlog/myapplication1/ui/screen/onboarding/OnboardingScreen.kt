package com.healthlog.myapplication1.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.domain.util.BmrCalculator
import com.healthlog.myapplication1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit
) {
    val saved by viewModel.saved.collectAsState()
    val existing by viewModel.existing.collectAsState()

    var name     by remember { mutableStateOf(existing?.name ?: "") }
    var ageStr   by remember { mutableStateOf(existing?.age?.toString() ?: "") }
    var gender   by remember { mutableStateOf(existing?.gender ?: "MALE") }
    var heightStr by remember { mutableStateOf(existing?.height?.toString() ?: "") }
    var weightStr by remember { mutableStateOf(existing?.weight?.toString() ?: "") }
    var goal     by remember { mutableStateOf(existing?.goal ?: "MAINTAIN") }
    var activityIndex by remember { mutableStateOf(0) }

    LaunchedEffect(existing) {
        existing?.let {
            name = it.name
            ageStr = it.age.toString()
            gender = it.gender
            heightStr = it.height.toString()
            weightStr = it.weight.toString()
            goal = it.goal
            activityIndex = when {
                it.activityLevel <= 1.2f   -> 0
                it.activityLevel <= 1.375f -> 1
                it.activityLevel <= 1.55f  -> 2
                it.activityLevel <= 1.725f -> 3
                else                       -> 4
            }
        }
    }

    LaunchedEffect(saved) { if (saved) onComplete() }

    val activityLevels = listOf(1.2f, 1.375f, 1.55f, 1.725f, 1.9f)
    val activityLabels = activityLevels.map { BmrCalculator.activityLabel(it) }

    val bmrPreview = remember(ageStr, gender, heightStr, weightStr) {
        val age    = ageStr.toIntOrNull() ?: 0
        val height = heightStr.toFloatOrNull() ?: 0f
        val weight = weightStr.toFloatOrNull() ?: 0f
        if (age > 0 && height > 0 && weight > 0)
            BmrCalculator.calculateBmr(gender, weight, height, age).toInt()
        else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("HealthLog", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
        Text("기본 정보를 입력해주세요", fontSize = 14.sp, color = TextSecondary)
        Spacer(Modifier.height(8.dp))

        // 이름
        AppTextField(value = name, onValueChange = { name = it }, label = "이름")

        // 나이 + 성별
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(
                value = ageStr,
                onValueChange = { ageStr = it },
                label = "나이",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("성별", fontSize = 11.sp, color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MALE" to "남", "FEMALE" to "여").forEach { (key, label) ->
                        FilterChip(
                            selected = gender == key,
                            onClick = { gender = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenDim,
                                selectedLabelColor = GreenAccent
                            )
                        )
                    }
                }
            }
        }

        // 키 + 체중
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppTextField(
                value = heightStr, onValueChange = { heightStr = it },
                label = "키 (cm)", keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
            AppTextField(
                value = weightStr, onValueChange = { weightStr = it },
                label = "몸무게 (kg)", keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }

        // 목표
        Text("목표", fontSize = 11.sp, color = TextSecondary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("LOSS" to "감량", "MAINTAIN" to "유지", "GAIN" to "증량").forEach { (key, label) ->
                FilterChip(
                    selected = goal == key,
                    onClick = { goal = key },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDim,
                        selectedLabelColor = GreenAccent
                    )
                )
            }
        }

        // 활동량
        Text("활동량", fontSize = 11.sp, color = TextSecondary)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            activityLabels.forEachIndexed { idx, label ->
                Surface(
                    onClick = { activityIndex = idx },
                    shape = RoundedCornerShape(10.dp),
                    color = if (activityIndex == idx) GreenDim else BgCard,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = activityIndex == idx,
                            onClick = { activityIndex = idx },
                            colors = RadioButtonDefaults.colors(selectedColor = GreenAccent)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, fontSize = 13.sp, color = TextPrimary)
                    }
                }
            }
        }

        // BMR 미리보기
        if (bmrPreview != null) {
            val tdee = BmrCalculator.calculateTdee(bmrPreview.toFloat(), activityLevels[activityIndex]).toInt()
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GreenDim,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PreviewStat("기초대사량", "${bmrPreview} kcal")
                    PreviewStat("유지 칼로리", "${tdee} kcal")
                    PreviewStat(
                        "목표 칼로리",
                        "${BmrCalculator.targetCalories(tdee.toFloat(), goal).toInt()} kcal"
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                val age    = ageStr.toIntOrNull() ?: return@Button
                val height = heightStr.toFloatOrNull() ?: return@Button
                val weight = weightStr.toFloatOrNull() ?: return@Button
                if (name.isBlank()) return@Button
                viewModel.save(name, age, gender, height, weight, goal, activityLevels[activityIndex])
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = BgBase)
        ) {
            Text("시작하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenAccent,
            unfocusedBorderColor = Border2,
            focusedLabelColor = GreenAccent,
            unfocusedLabelColor = TextSecondary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = GreenAccent,
            focusedContainerColor = BgCard,
            unfocusedContainerColor = BgCard
        ),
        singleLine = true
    )
}

@Composable
private fun PreviewStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
    }
}
