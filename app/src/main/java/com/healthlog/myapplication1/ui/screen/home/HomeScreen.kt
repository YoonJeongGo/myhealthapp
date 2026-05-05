package com.healthlog.myapplication1.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.data.local.entity.*
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.screen.input.*
import com.healthlog.myapplication1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    weightInputViewModel: WeightInputViewModel,
    bodyFatInputViewModel: BodyFatInputViewModel,
    exerciseInputViewModel: ExerciseInputViewModel,
    mealInputViewModel: MealInputViewModel,
    onSettingsClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var sheet by remember { mutableStateOf(InputSheetType.NONE) }

    if (sheet != InputSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { sheet = InputSheetType.NONE },
            containerColor = BgSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            when (sheet) {
                InputSheetType.WEIGHT   -> WeightInputSheet(
                    viewModel = weightInputViewModel, date = state.date,
                    onDone = { sheet = InputSheetType.NONE; viewModel.loadToday() }
                )
                InputSheetType.BODY_FAT -> BodyFatInputSheet(
                    viewModel = bodyFatInputViewModel, date = state.date,
                    onDone = { sheet = InputSheetType.NONE; viewModel.loadToday() }
                )
                InputSheetType.EXERCISE -> ExerciseInputSheet(
                    viewModel = exerciseInputViewModel, date = state.date,
                    onDone = { sheet = InputSheetType.NONE; viewModel.loadToday() }
                )
                InputSheetType.MEAL     -> MealInputSheet(
                    viewModel = mealInputViewModel, date = state.date,
                    onDone = { sheet = InputSheetType.NONE; viewModel.loadToday() }
                )
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .verticalScroll(rememberScrollState())
    ) {
        // ── 앱바 ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(DateUtils.toDisplayFull(state.date), fontSize = 12.sp, color = TextTertiary)
                Text("HealthLog", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "설정", tint = TextSecondary)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // ── 칼로리 카드 ────────────────────────────
            CalorieCard(
                consumed = state.todayLog?.totalCalories ?: 0,
                target = state.targetCalories,
            )

            // ── 2x2 요약 그리드 ────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(
                    label = "몸무게",
                    value = state.todayLog?.latestWeight?.let { "%.1f kg".format(it) } ?: "미기록",
                    valueColor = if (state.todayLog?.latestWeight != null) GreenAccent else TextTertiary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "체지방률",
                    value = state.todayLog?.latestBodyFat?.let { "%.1f%%".format(it) } ?: "미기록",
                    valueColor = if (state.todayLog?.latestBodyFat != null) TextPrimary else TextTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(
                    label = "오늘 운동",
                    value = if (state.todayExercises.isNotEmpty())
                        state.todayExercises.first().exerciseName else "없음",
                    valueColor = if (state.todayExercises.isNotEmpty()) InfoBlue else TextTertiary,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "총 칼로리",
                    value = "${state.todayLog?.totalCalories ?: 0} kcal",
                    valueColor = GreenAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── 식사 요약 ──────────────────────────────
            if (state.todayMeals.isNotEmpty()) {
                MealSummaryCard(meals = state.todayMeals)
            }

            // ── 빠른 기록 ──────────────────────────────
            Text("빠른 기록", fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("체중", "⚖️", GreenAccent, Modifier.weight(1f)) { sheet = InputSheetType.WEIGHT }
                QuickCard("식사", "🍽️", Warning, Modifier.weight(1f)) { sheet = InputSheetType.MEAL }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("운동", "🏃", InfoBlue, Modifier.weight(1f)) { sheet = InputSheetType.EXERCISE }
                QuickCard("체지방", "📊", Purple, Modifier.weight(1f)) { sheet = InputSheetType.BODY_FAT }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CalorieCard(consumed: Int, target: Int) {
    val progress = if (target > 0) (consumed.toFloat() / target).coerceIn(0f, 1f) else 0f
    val percent = (progress * 100).toInt()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BgCard,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(listOf(Color(0x1F3ECF8E), Color(0x0F5B9CF6))),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("오늘 목표 칼로리", fontSize = 11.sp, color = TextTertiary)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$consumed", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                            Text(" / $target kcal", fontSize = 13.sp, color = TextSecondary,
                                modifier = Modifier.padding(bottom = 3.dp))
                        }
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = GreenDim) {
                        Text("목표 $percent%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenAccent)
                    }
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(5.dp),
                    color = if (progress >= 1f) Danger else GreenAccent,
                    trackColor = BgBase,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                if (target > consumed) {
                    Text("남은 칼로리 ${target - consumed} kcal", fontSize = 10.sp,
                        color = TextTertiary, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, valueColor: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = BgCard, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = TextTertiary, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = valueColor, maxLines = 1)
        }
    }
}

@Composable
private fun MealSummaryCard(meals: List<MealRecordWithItems>) {
    Surface(shape = RoundedCornerShape(14.dp), color = BgCard, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("오늘 식사", fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            meals.forEach { mri ->
                val typeLabel = when (mri.record.mealType) {
                    "BREAKFAST" -> "아침"; "LUNCH" -> "점심"; "DINNER" -> "저녁"; else -> "간식"
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(GreenAccent, RoundedCornerShape(3.dp)))
                        Spacer(Modifier.width(8.dp))
                        Text(typeLabel, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                    Text("${mri.record.totalCalories} kcal", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun QuickCard(label: String, emoji: String, accentColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(14.dp), color = BgCard, modifier = modifier) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 18.sp) }
            }
            Column {
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("탭해서 입력", fontSize = 10.sp, color = TextTertiary)
            }
        }
    }
}
