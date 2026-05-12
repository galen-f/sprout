package com.example.sprout.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sprout.domain.model.WateringStatus
import com.example.sprout.ui.theme.SproutDew
import com.example.sprout.ui.theme.SproutGreen
import com.example.sprout.ui.theme.SproutSoil

@Composable
fun StatusPill(status: WateringStatus, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when (status) {
        WateringStatus.NeverWatered -> Triple("Never watered", SproutSoil, Color.White)
        WateringStatus.DueToday -> Triple("Water today", SproutGreen, Color.White)
        is WateringStatus.DueIn -> Triple("Due in ${status.days}d", SproutDew, SproutGreen)
        is WateringStatus.OverdueBy -> Triple("Overdue ${status.days}d", Color(0xFFB71C1C), Color.White)
    }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = bgColor,
        tonalElevation = 0.dp,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
