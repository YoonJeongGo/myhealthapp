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
import com.healthlog.myapplication1.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseInputSheet(
    viewModel: ExerciseInputViewModel,
    date: String,
    onDone: () -> Unit
) {
    val saveState by viewModel.saveState.collectAsState()

    val exerciseTypes = listOf("달리기", "걷기", "웨이트", "수영", "자전거", "기타")
    var selectedType by remember { mutableStateOf("달리기") }
    var customType   by remember { mutableStateOf("") }
    var durationMin  by remember { mutableStateOf(30) }
    var memo         by remember { mutableStateOf("") }

    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) { viewModel.reset(); onDone() }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .navigationBarsPadding()
    ) {
        Spacer(Modifier.height(8.dp))
        Text("운동 기록", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(DateUtils.toDisplayShort(date), fontSize = 12.sp, color = TextTertiary)

        Spacer(Modifier.height(20.dp))

        // 운동 종류 칩
        Text("운동 종류", fontSize = 11.sp, color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            exerciseTypes.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDim,
                        selectedLabelColor = GreenAccent
                    )
                )
            }
        }

        if (selectedType == "기타") {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = customType,
                onValueChange = { customType = it },
                label = { Text("직접 입력") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenAccent,
                    unfocusedBorderColor = Border2,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard
                ),
                singleLine = true
            )
        }

        Spacer(Modifier.height(16.dp))

        // 운동 시간
        Text("운동 시간", fontSize = 11.sp, color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                onClick = { if (durationMin > 5) durationMin -= 5 },
                shape = RoundedCornerShape(10.dp),
                color = BgCard,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("−", fontSize = 20.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Text("$durationMin 분", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = InfoBlue)
            Surface(
                onClick = { durationMin += 5 },
                shape = RoundedCornerShape(10.dp),
                color = GreenDim,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 20.sp, color = GreenAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모 (선택)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenAccent,
                unfocusedBorderColor = Border2,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BgCard,
                unfocusedContainerColor = BgCard
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val name = if (selectedType == "기타" && customType.isNotBlank()) customType else selectedType
                viewModel.save(
                    date = date,
                    exerciseName = name,
                    duration = durationMin,
                    calories = 0,
                    note = memo.ifBlank { null }
                )
            },
            enabled = saveState !is SaveState.Loading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = InfoBlue, contentColor = BgBase)
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
