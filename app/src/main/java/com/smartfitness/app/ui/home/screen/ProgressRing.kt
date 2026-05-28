package com.smartfitness.app.ui.home.screen


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartfitness.app.core.theme.TextPrimary



@Composable
fun ProgressRing(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000), label = ""
    )
    val animatedColor by animateColorAsState(
        targetValue = getProgressColor(progress),
        animationSpec = tween(500), label = ""
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) {
            drawArc(
                color = Color.Gray.copy(0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = animatedColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            "${(progress * 100).toInt()}%",
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }

}


fun getProgressColor(progress: Float): Color {
    return when {
        progress < 0.3f -> Color(0xFFFF3B30)   // Red
        progress < 0.6f -> Color(0xFFFF9500)   // Orange
        else -> Color(0xFF00C853)              // Green
    }
}
