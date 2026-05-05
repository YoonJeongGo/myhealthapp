package com.healthlog.myapplication1.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.data.local.entity.UserProfileEntity
import com.healthlog.myapplication1.domain.util.BmrCalculator
import com.healthlog.myapplication1.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onEditProfile: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBase)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("설정", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(Modifier.height(20.dp))

        if (profile != null) {
            ProfileCard(profile!!, onEditProfile)
        } else {
            Surface(shape = RoundedCornerShape(16.dp), color = BgCard, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("프로필 없음", color = TextTertiary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (profile != null) {
            BmrCard(profile!!)
        }

        Spacer(Modifier.height(16.dp))

        // 앱 정보
        Surface(shape = RoundedCornerShape(14.dp), color = BgCard, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("앱 정보", fontSize = 12.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp))
                InfoRow("버전", "1.0.0")
                InfoRow("데이터 저장", "기기 내부 (로컬 전용)")
                InfoRow("AI 기능", "OpenAI GPT-4o-mini")
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: UserProfileEntity, onEdit: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BgCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(GreenDim, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(1),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenAccent
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "${profile.age}세 · ${if (profile.gender == "MALE") "남성" else "여성"} · ${profile.height.toInt()}cm",
                    fontSize = 12.sp, color = TextSecondary
                )
                Text(
                    "목표: ${BmrCalculator.goalLabel(profile.goal)}",
                    fontSize = 11.sp, color = GreenAccent
                )
            }
            TextButton(onClick = onEdit) {
                Text("수정", color = GreenAccent, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun BmrCard(profile: UserProfileEntity) {
    val target = BmrCalculator.targetCalories(profile.tdee, profile.goal).toInt()
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BgCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("내 정보", fontSize = 12.sp, color = TextTertiary, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BmrStat("기초대사량", "${profile.bmr.toInt()} kcal", Modifier.weight(1f))
                BmrStat("유지 칼로리", "${profile.tdee.toInt()} kcal", Modifier.weight(1f))
                BmrStat("목표 칼로리", "$target kcal", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            InfoRow("활동량", BmrCalculator.activityLabel(profile.activityLevel))
        }
    }
}

@Composable
private fun BmrStat(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
        Text(label, fontSize = 10.sp, color = TextTertiary)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}
