package com.smartfitness.app.ui.steps

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.core.theme.*
import com.smartfitness.app.core.ui.components.SmartFitnessTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsScreen(onBack: () -> Unit = {}) {

    val viewModel: StepsViewModel = hiltViewModel()
    val todaySteps by viewModel.todaySteps.collectAsState()
    val records by viewModel.records.collectAsState()
    val goal = viewModel.goal
    val progress = (todaySteps.toFloat() / goal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(progress, tween(600), label = "steps")

    var isTracking by remember { mutableStateOf(false) }

    // Permission launcher for ACTIVITY_RECOGNITION (Android 10+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isTracking = true
            viewModel.startService()
        }
    }

    val stepColor = Color(0xFF4CAF50)

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            SmartFitnessTopBar(
                title = "Step Counter",
                showBackButton = true,
                onBackClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Progress Card ---
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardColor),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Today's Steps", color = TextSecondary, fontSize = 13.sp)
                        Spacer(Modifier.height(20.dp))

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.size(180.dp),
                                color = stepColor.copy(alpha = 0.12f),
                                strokeWidth = 14.dp,
                                strokeCap = StrokeCap.Round
                            )
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(180.dp),
                                color = stepColor,
                                strokeWidth = 14.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = stepColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "$todaySteps",
                                    color = TextPrimary,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("of $goal steps", color = TextSecondary, fontSize = 12.sp)
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StepStat("Steps", "$todaySteps", stepColor)
                            StepStat("Distance", "${"%.2f".format(todaySteps * 0.00076)} km", Color(0xFF0288D1))
                            StepStat("Calories", "${(todaySteps * 0.04).toInt()} kcal", Color(0xFFEF4444))
                            StepStat("Goal", "${(progress * 100).toInt()}%", BrandColor)
                        }
                    }
                }
            }

            // --- Start/Stop Button ---
            item {
                Button(
                    onClick = {
                        if (isTracking) {
                            isTracking = false
                            viewModel.stopService()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                            } else {
                                isTracking = true
                                viewModel.startService()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) Color(0xFFEF4444) else stepColor
                    )
                ) {
                    Icon(
                        imageVector = if (isTracking) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isTracking) "Stop Tracking" else "Start Tracking",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // --- History ---
            item {
                Text(
                    "Step History",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            if (records.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "No records yet. Start tracking to record steps.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(records) { record ->
                    StepHistoryItem(record.date, record.steps, goal)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun StepStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun StepHistoryItem(date: String, steps: Int, goal: Int) {
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val displayDate = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsed = sdf.parse(date)
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(parsed ?: Date())
    } catch (e: Exception) { date }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF4CAF50).copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayDate, color = TextSecondary, fontSize = 12.sp)
                    Text("$steps steps", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                Text(
                    "${(progress * 100).toInt()}%",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).padding(horizontal = 2.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF4CAF50).copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

