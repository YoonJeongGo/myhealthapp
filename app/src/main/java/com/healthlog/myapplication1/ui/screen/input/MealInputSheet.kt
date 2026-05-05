package com.healthlog.myapplication1.ui.screen.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.data.remote.dto.ErrorType
import com.healthlog.myapplication1.data.remote.dto.ParsedMealItem
import com.healthlog.myapplication1.data.repository.MealItemInput
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealInputSheet(
    viewModel: MealInputViewModel,
    date: String,
    onDone: () -> Unit
) {
    val aiState   by viewModel.aiState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    val mealTypes = listOf("BREAKFAST" to "아침", "LUNCH" to "점심", "DINNER" to "저녁", "SNACK" to "간식")
    var selectedMeal by remember { mutableStateOf("BREAKFAST") }
    var rawInput     by remember { mutableStateOf("") }

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) { viewModel.resetSaveState(); onDone() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(8.dp))
        Text("식사 기록", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(DateUtils.toDisplayShort(date), fontSize = 12.sp, color = TextTertiary)

        Spacer(Modifier.height(16.dp))

        // 끼니 탭
        ScrollableTabRow(
            selectedTabIndex = mealTypes.indexOfFirst { it.first == selectedMeal },
            containerColor = BgCard,
            contentColor = GreenAccent,
            indicator = {},
            divider = {}
        ) {
            mealTypes.forEach { (key, label) ->
                Tab(
                    selected = selectedMeal == key,
                    onClick = { selectedMeal = key },
                    text = {
                        Text(
                            label,
                            fontSize = 13.sp,
                            fontWeight = if (selectedMeal == key) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedMeal == key) GreenAccent else TextTertiary
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 텍스트 입력
        OutlinedTextField(
            value = rawInput,
            onValueChange = { rawInput = it },
            label = { Text("먹은 음식을 자유롭게 입력하세요") },
            placeholder = { Text("예: 현미밥 반 공기, 된장찌개, 두부 한 모", fontSize = 12.sp, color = TextTertiary) },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenAccent,
                unfocusedBorderColor = Border2,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BgCard,
                unfocusedContainerColor = BgCard
            )
        )
        Text("AI가 칼로리를 자동으로 계산합니다", fontSize = 10.sp, color = TextTertiary,
            modifier = Modifier.align(Alignment.End).padding(top = 4.dp))

        Spacer(Modifier.height(12.dp))

        // AI 분석 버튼
        Button(
            onClick = { viewModel.analyzeWithAi(rawInput) },
            enabled = rawInput.isNotBlank() && aiState !is MealAiState.Loading,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = BgBase)
        ) {
            if (aiState is MealAiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BgBase, strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("AI 분석 중...", fontWeight = FontWeight.SemiBold)
            } else {
                Text("AI 칼로리 분석", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // AI 결과 표시
        when (val s = aiState) {
            is MealAiState.Success -> {
                AiResultSection(
                    items = s.items,
                    totalCalories = s.totalCalories,
                    onSave = {
                        viewModel.saveMeal(
                            date = date,
                            mealType = selectedMeal,
                            rawInput = rawInput,
                            items = s.items.map { MealItemInput(it.name, it.calories, it.unit, it.isEstimated) },
                            isAiEstimated = true
                        )
                    },
                    onRetry = { viewModel.resetAiState() }
                )
            }
            is MealAiState.Error -> {
                AiErrorSection(
                    type = s.type,
                    rawText = s.rawText,
                    onRetry = { viewModel.resetAiState() }
                )
            }
            else -> {}
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AiResultSection(
    items: List<ParsedMealItem>,
    totalCalories: Int,
    onSave: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = GreenDim2,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("AI 추정 결과", fontSize = 10.sp, color = GreenAccent,
                fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
            Spacer(Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${item.name}${if (item.unit != null) " (${item.unit})" else ""}",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${item.calories} kcal", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = GreenAccent)
                        if (item.isEstimated) {
                            Spacer(Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Warning.copy(alpha = 0.15f)
                            ) {
                                Text("추정", modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    fontSize = 9.sp, color = Warning, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = GreenAccent.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("합계", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("$totalCalories kcal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
            }
        }
    }

    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedButton(
            onClick = onRetry,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = BgCard,
                contentColor = TextSecondary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border2)
        ) { Text("다시 입력") }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent, contentColor = BgBase)
        ) { Text("이대로 저장", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun AiErrorSection(type: ErrorType, rawText: String?, onRetry: () -> Unit) {
    val (msg, color) = when (type) {
        ErrorType.PARSE_FAILED  -> "AI가 음식을 인식하지 못했어요.\n직접 입력 모드로 전환합니다." to Warning
        ErrorType.NETWORK_ERROR -> "인터넷 연결을 확인해주세요.\n오프라인에서는 직접 입력하세요." to Danger
        ErrorType.API_LIMIT     -> "AI 사용 한도를 잠시 초과했어요.\n직접 입력하거나 잠시 후 다시 시도해주세요." to Warning
        ErrorType.UNKNOWN       -> "오류가 발생했어요. 직접 입력해주세요." to Danger
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(msg, fontSize = 12.sp, color = color, lineHeight = 18.sp)
        }
    }

    Spacer(Modifier.height(8.dp))

    Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BgCard, contentColor = TextSecondary)
    ) { Text("다시 시도") }
}
