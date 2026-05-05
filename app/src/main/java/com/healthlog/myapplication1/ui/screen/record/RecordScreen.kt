package com.healthlog.myapplication1.ui.screen.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.data.local.entity.*
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.screen.input.*
import com.healthlog.myapplication1.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: RecordViewModel,
    weightVm: WeightInputViewModel,
    bodyFatVm: BodyFatInputViewModel,
    exerciseVm: ExerciseInputViewModel,
    mealVm: MealInputViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val date = state.date
    var sheet by remember { mutableStateOf(InputSheetType.NONE) }

    if (sheet != InputSheetType.NONE) {
        ModalBottomSheet(
            onDismissRequest = { sheet = InputSheetType.NONE },
            containerColor = BgSurface,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            when (sheet) {
                InputSheetType.WEIGHT   -> WeightInputSheet(weightVm, date) { sheet = InputSheetType.NONE; viewModel.loadForDate(date) }
                InputSheetType.BODY_FAT -> BodyFatInputSheet(bodyFatVm, date) { sheet = InputSheetType.NONE; viewModel.loadForDate(date) }
                InputSheetType.EXERCISE -> ExerciseInputSheet(exerciseVm, date) { sheet = InputSheetType.NONE; viewModel.loadForDate(date) }
                InputSheetType.MEAL     -> MealInputSheet(mealVm, date) { sheet = InputSheetType.NONE; viewModel.loadForDate(date) }
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = BgBase,
        topBar = {
            Column(modifier = Modifier.background(BgBase).padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text("기록", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(DateUtils.toDisplayFull(date), fontSize = 12.sp, color = TextTertiary)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { sheet = InputSheetType.MEAL },
                containerColor = GreenAccent,
                contentColor = BgBase
            ) {
                Icon(Icons.Default.Add, contentDescription = "기록 추가")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 몸무게 섹션
            if (state.weights.isNotEmpty()) {
                item {
                    SectionHeader("몸무게") { sheet = InputSheetType.WEIGHT }
                }
                items(state.weights) { w ->
                    RecordCard(
                        emoji = "⚖️",
                        title = "${"%.1f".format(w.weight)} kg",
                        subtitle = w.note,
                        accentColor = GreenAccent,
                        onDelete = { viewModel.deleteWeight(w) }
                    )
                }
            }

            // 체지방 섹션
            if (state.bodyFats.isNotEmpty()) {
                item { SectionHeader("체지방률") { sheet = InputSheetType.BODY_FAT } }
                items(state.bodyFats) { bf ->
                    RecordCard(
                        emoji = "📊",
                        title = "${"%.1f".format(bf.bodyFat)}%",
                        subtitle = bf.note,
                        accentColor = Purple,
                        onDelete = { viewModel.deleteBodyFat(bf) }
                    )
                }
            }

            // 식사 섹션
            if (state.meals.isNotEmpty()) {
                item { SectionHeader("식사") { sheet = InputSheetType.MEAL } }
                items(state.meals) { mri ->
                    val typeLabel = when (mri.record.mealType) {
                        "BREAKFAST" -> "아침"; "LUNCH" -> "점심"
                        "DINNER" -> "저녁"; else -> "간식"
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BgCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🍽️ $typeLabel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${mri.record.totalCalories} kcal", fontSize = 13.sp,
                                        color = GreenAccent, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { viewModel.deleteMealRecord(mri.record) },
                                        modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "삭제",
                                            tint = TextTertiary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            if (mri.items.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                mri.items.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(item.name, fontSize = 11.sp, color = TextSecondary)
                                        Text("${item.calories} kcal", fontSize = 11.sp, color = TextTertiary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 운동 섹션
            if (state.exercises.isNotEmpty()) {
                item { SectionHeader("운동") { sheet = InputSheetType.EXERCISE } }
                items(state.exercises) { ex ->
                    RecordCard(
                        emoji = "🏃",
                        title = "${ex.exerciseName} ${ex.duration}분",
                        subtitle = ex.note,
                        accentColor = InfoBlue,
                        onDelete = { viewModel.deleteExercise(ex) }
                    )
                }
            }

            if (state.weights.isEmpty() && state.bodyFats.isEmpty() &&
                state.meals.isEmpty() && state.exercises.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("오늘 기록이 없어요", fontSize = 15.sp, color = TextSecondary)
                            Text("아래 + 버튼으로 기록을 추가하세요", fontSize = 12.sp, color = TextTertiary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp)
        TextButton(onClick = onAdd, contentPadding = PaddingValues(0.dp)) {
            Text("+ 추가", fontSize = 11.sp, color = GreenAccent)
        }
    }
}

@Composable
private fun RecordCard(
    emoji: String,
    title: String,
    subtitle: String?,
    accentColor: androidx.compose.ui.graphics.Color,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BgCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (!subtitle.isNullOrBlank()) {
                    Text(subtitle, fontSize = 11.sp, color = TextTertiary)
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "삭제",
                    tint = TextTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
