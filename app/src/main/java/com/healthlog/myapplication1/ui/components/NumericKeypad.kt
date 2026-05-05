package com.healthlog.myapplication1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthlog.myapplication1.ui.theme.*

@Composable
fun NumericKeypad(
    value: String,
    onValueChange: (String) -> Unit,
    allowDecimal: Boolean = true,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(if (allowDecimal) "." else "", "0", "⌫")
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        KeyButton(
                            label = key,
                            modifier = Modifier.weight(1f),
                            isDelete = key == "⌫"
                        ) {
                            when (key) {
                                "⌫" -> {
                                    if (value.isNotEmpty()) onValueChange(value.dropLast(1))
                                }
                                "." -> {
                                    if (!value.contains('.')) onValueChange("$value.")
                                }
                                else -> {
                                    if (value == "0") onValueChange(key)
                                    else if (value.length < 6) onValueChange("$value$key")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    isDelete: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isDelete) BgSurface else BgCard,
        tonalElevation = 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDelete) TextSecondary else TextPrimary
            )
        }
    }
}
