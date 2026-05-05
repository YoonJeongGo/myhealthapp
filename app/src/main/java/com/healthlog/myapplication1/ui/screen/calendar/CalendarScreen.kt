package com.healthlog.myapplication1.ui.screen.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.data.local.entity.DailyLogEntity
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.theme.*

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val yearMonth  by viewModel.yearMonth.collectAsState()
    val logs       by viewModel.logs.collectAsState()
    val selected   by viewModel.selectedDate.collectAsState()
    val selLog     by viewModel.selectedLog.collectAsState()
    val today      = DateUtils.today()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // 월 네비게이션
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.prevMonth() }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달", tint = TextSecondary)
            }
            Text(
                DateUtils.toDisplayMonth(DateUtils.monthRange(yearMonth).first),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달", tint = TextSecondary)
            }
        }

        // 요일 헤더
        val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEachIndexed { i, label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = if (i == 0) Danger else TextTertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 달력 그리드
        val days = DateUtils.daysInMonth(yearMonth)
        val firstDow = DateUtils.firstDayOfWeek(yearMonth)
        val cells = firstDow + days + (7 - (firstDow + days) % 7) % 7

        for (week in 0 until cells / 7) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dow in 0..6) {
                    val idx = week * 7 + dow
                    val day = idx - firstDow + 1
                    if (day < 1 || day > days) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val dateStr = DateUtils.dateOf(yearMonth, day)
                        val log = logs[dateStr]
                        val isToday = dateStr == today
                        val isSelected = dateStr == selected
                        DayCell(
                            day = day,
                            isToday = isToday,
                            isSelected = isSelected,
                            isSunday = dow == 0,
                            log = log,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.selectDate(dateStr) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        // 범례
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(GreenAccent, "몸무게")
            LegendItem(InfoBlue, "운동")
            LegendItem(Warning, "식사")
        }

        // 선택된 날짜 요약
        if (selLog != null || selected.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            SelectedDaySummary(date = selected, log = selLog)
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    isSunday: Boolean,
    log: DailyLogEntity?,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(if (isSelected) Modifier.background(GreenDim) else Modifier)
            .then(if (isToday && !isSelected) Modifier.background(GreenDim.copy(alpha = 0.5f)) else Modifier)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = if (isToday) 13.sp else 12.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> GreenAccent
                    isToday    -> GreenAccent
                    isSunday   -> Danger.copy(alpha = 0.8f)
                    else       -> TextSecondary
                }
            )
            if (log != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    if (log.latestWeight != null) Dot(GreenAccent)
                    if (log.hasExercise)          Dot(InfoBlue)
                    if (log.totalCalories > 0)    Dot(Warning)
                }
            }
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Dot(color)
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = TextTertiary)
    }
}

@Composable
private fun SelectedDaySummary(date: String, log: DailyLogEntity?) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BgCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                DateUtils.toDisplayFull(date),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenAccent
            )
            Spacer(Modifier.height(8.dp))
            if (log == null) {
                Text("기록 없음", fontSize = 12.sp, color = TextTertiary)
            } else {
                if (log.latestWeight != null) {
                    SummaryRow("몸무게", "${"%.1f".format(log.latestWeight)} kg")
                }
                if (log.latestBodyFat != null) {
                    SummaryRow("체지방률", "${"%.1f".format(log.latestBodyFat)}%")
                }
                if (log.totalCalories > 0) {
                    SummaryRow("섭취 칼로리", "${log.totalCalories} kcal")
                }
                if (log.hasExercise) {
                    SummaryRow("운동", "✓")
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
