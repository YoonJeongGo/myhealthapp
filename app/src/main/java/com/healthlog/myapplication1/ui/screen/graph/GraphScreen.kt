package com.healthlog.myapplication1.ui.screen.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.domain.util.DateUtils
import com.healthlog.myapplication1.ui.theme.*

@Composable
fun GraphScreen(viewModel: GraphViewModel) {
    val state by viewModel.uiState.collectAsState()

    val dataPoints: List<Pair<String, Float>> = when (state.category) {
        GraphCategory.WEIGHT   -> state.weights.map { it.date to it.weight }
        GraphCategory.BODY_FAT -> state.bodyFats.map { it.date to it.bodyFat }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("통계", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(16.dp))

        // 카테고리 선택
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GraphCategory.values().forEach { cat ->
                FilterChip(
                    selected = state.category == cat,
                    onClick = { viewModel.setCategory(cat) },
                    label = { Text(if (cat == GraphCategory.WEIGHT) "몸무게" else "체지방률", fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDim,
                        selectedLabelColor = GreenAccent
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 기간 탭
        TabRow(
            selectedTabIndex = GraphPeriod.values().indexOf(state.period),
            containerColor = BgCard,
            contentColor = GreenAccent,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            GraphPeriod.values().forEach { p ->
                Tab(
                    selected = state.period == p,
                    onClick = { viewModel.setPeriod(p) },
                    text = {
                        Text(p.label, fontSize = 12.sp,
                            fontWeight = if (state.period == p) FontWeight.Bold else FontWeight.Normal)
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 요약 수치
        if (dataPoints.isNotEmpty()) {
            val current = dataPoints.last().second
            val first   = dataPoints.first().second
            val delta   = current - first
            val unit    = if (state.category == GraphCategory.WEIGHT) "kg" else "%"

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("현재", "${"%.1f".format(current)} $unit", GreenAccent, Modifier.weight(1f))
                StatCard("${state.period.label} 전", "${"%.1f".format(first)} $unit", TextSecondary, Modifier.weight(1f))
                StatCard("변화", "${if (delta >= 0) "+" else ""}${"%.1f".format(delta)} $unit",
                    if (delta < 0) GreenAccent else Warning, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // 선 그래프
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BgCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (dataPoints.size >= 2) {
                        LineChart(
                            points = dataPoints,
                            lineColor = GreenAccent,
                            modifier = Modifier.fillMaxWidth().height(160.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("데이터가 부족합니다", fontSize = 13.sp, color = TextTertiary)
                        }
                    }

                    // X축 라벨
                    if (dataPoints.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(DateUtils.toDisplayShort(dataPoints.first().first), fontSize = 9.sp, color = TextTertiary)
                            Text(DateUtils.toDisplayShort(dataPoints.last().first), fontSize = 9.sp, color = TextTertiary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 최근 기록 목록
            Text("최근 기록", fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            dataPoints.reversed().take(7).forEach { (date, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(DateUtils.toDisplayFull(date), fontSize = 12.sp, color = TextSecondary)
                    Text(
                        "${"%.1f".format(value)} ${if (state.category == GraphCategory.WEIGHT) "kg" else "%"}",
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary
                    )
                }
                HorizontalDivider(color = Border, thickness = 0.5.dp)
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📊", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("기록이 없어요", fontSize = 15.sp, color = TextSecondary)
                    Text("홈에서 기록을 추가해보세요", fontSize = 12.sp, color = TextTertiary)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, valueColor: Color, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = BgCard, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = TextTertiary)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun LineChart(
    points: List<Pair<String, Float>>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val min = points.minOf { it.second }
    val max = points.maxOf { it.second }
    val range = (max - min).let { if (it < 0.01f) 1f else it }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padding = 16.dp.toPx()
        val chartW = w - padding * 2
        val chartH = h - padding * 2

        // 격자 라인
        for (i in 0..3) {
            val y = padding + chartH * i / 3
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(padding, y),
                end = Offset(w - padding, y),
                strokeWidth = 1f
            )
        }

        if (points.size < 2) return@Canvas

        // 라인 경로
        val path = Path()
        val fillPath = Path()
        val coords = points.mapIndexed { i, (_, v) ->
            val x = padding + chartW * i / (points.size - 1)
            val y = padding + chartH * (1f - (v - min) / range)
            Offset(x, y)
        }

        coords.forEachIndexed { i, offset ->
            if (i == 0) {
                path.moveTo(offset.x, offset.y)
                fillPath.moveTo(offset.x, h)
                fillPath.lineTo(offset.x, offset.y)
            } else {
                path.lineTo(offset.x, offset.y)
                fillPath.lineTo(offset.x, offset.y)
            }
        }

        fillPath.lineTo(coords.last().x, h)
        fillPath.close()

        // 채우기
        drawPath(
            path = fillPath,
            color = lineColor.copy(alpha = 0.08f)
        )

        // 라인
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 포인트
        coords.forEachIndexed { i, offset ->
            val isLast = i == coords.size - 1
            drawCircle(
                color = lineColor,
                radius = if (isLast) 5.dp.toPx() else 3.dp.toPx(),
                center = offset
            )
            if (isLast) {
                drawCircle(
                    color = Color.Black.copy(alpha = 0.8f),
                    radius = 2.dp.toPx(),
                    center = offset
                )
            }
        }
    }
}

