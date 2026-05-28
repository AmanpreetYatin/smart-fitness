package com.smartfitness.app.ui.workoutdetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.core.ui.components.SmartFitnessTopBar
import com.smartfitness.app.core.theme.Background
import com.smartfitness.app.core.theme.CardColor
import com.smartfitness.app.core.theme.CardSoft
import com.smartfitness.app.core.theme.LightPurple
import com.smartfitness.app.core.theme.TextPrimary
import com.smartfitness.app.core.theme.TextSecondary as AppTextSecondary
import com.smartfitness.app.core.utilities.HelperFunctions.formatTime

// Brand colors — aligned with SmartFitness theme (primary = #6650A4)
private val Primary = BrandColor                  // #6650A4 purple
private val PrimaryLight = Color(0xFF9C8FD4)      // lighter purple tint
private val ScreenBg = Background                 // #F5F7FF
private val CardBg = CardColor                    // White
private val CardBg2 = CardSoft                    // #EAF0FF soft lavender
private val TextSec = AppTextSecondary            // #6B7280

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
    onWorkoutComplete: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val workout = state.workout ?: return

    Scaffold(
        containerColor = ScreenBg,
        modifier = Modifier.navigationBarsPadding().statusBarsPadding(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            SmartFitnessTopBar(
                title = "",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favourite", tint = TextPrimary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ScreenBg)
                    .padding(16.dp)
            ) {
                if (state.isCompleted) {
                    Button(
                        onClick = onWorkoutComplete,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Workout Complete!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    Button(
                        onClick = { if (!state.isWorkoutStarted) viewModel.startWorkout() else viewModel.finishWorkout() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (state.isWorkoutStarted) "Finish Workout" else "Start Workout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Demo Video Section
            item { DemoVideoSection(workout.name) }

            // Title & Tags
            item { WorkoutTitleSection(workout) }

            // Stats Row
            item {
                StatsRow(
                    duration = workout.durationSeconds,
                    calories = workout.caloriesBurn,
                    sets = workout.sets.size,
                    difficulty = workout.difficulty
                )
            }

            // Timer Section
            item {
                TimerSection(
                    time = state.currentTime,
                    totalTime = workout.durationSeconds,
                    isRunning = state.isRunning,
                    onReset = { viewModel.resetTimer() }
                )
            }

            // Description
            item { DescriptionSection(workout.description) }

            // Sets
            item {
                Text(
                    text = "Exercise Sets",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(workout.sets) { set ->
                SetItem(set = set, onClick = { viewModel.toggleSetCompletion(set.setNumber) })
            }

            // Tips
            item { TipsSection() }
        }
    }
}

@Composable
fun DemoVideoSection(workoutName: String) {
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(listOf(Color(0xFF6650A4), Color(0xFF9C8FD4)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(180.dp).background(Color.White.copy(alpha = 0.06f), CircleShape))
        Box(modifier = Modifier.size(120.dp).background(Color.White.copy(alpha = 0.08f), CircleShape))

        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.25f),
            modifier = Modifier.size(90.dp)
        )

        // Play button
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play Demo",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Demo Video", color = Color.White, fontSize = 12.sp)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("0:30", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun WorkoutTitleSection(workout: WorkoutDetail) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = workout.name, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TagChip(label = workout.category, color = Primary)
            TagChip(label = workout.muscleGroup, color = Color(0xFF0288D1))
            TagChip(label = workout.difficulty, color = Color(0xFF4CAF50))
        }
    }
}

@Composable
fun TagChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatsRow(duration: Int, calories: Int, sets: Int, difficulty: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.Timer, value = "${duration}s", label = "Duration", iconColor = Primary)
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.LocalFireDepartment, value = "$calories", label = "Calories", iconColor = Color(0xFFE53935))
        StatCard(modifier = Modifier.weight(1f), icon = Icons.Default.FitnessCenter, value = "$sets", label = "Sets", iconColor = Color(0xFF0288D1))
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String, iconColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = TextSec, fontSize = 11.sp)
        }
    }
}

@Composable
fun TimerSection(time: Int, totalTime: Int, isRunning: Boolean, onReset: () -> Unit) {
    val progress = if (totalTime > 0) time.toFloat() / totalTime.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(300), label = "timer_progress")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(90.dp),
                    color = LightPurple,
                    strokeWidth = 7.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(90.dp),
                    color = Primary,
                    strokeWidth = 7.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = formatTime(time), color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        text = if (isRunning) "Active" else "Ready",
                        color = if (isRunning) Primary else TextSec,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Rest Timer", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("Time remaining for your rest period. Tap Start to begin.", color = TextSec, fontSize = 12.sp, lineHeight = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reset", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun DescriptionSection(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("How to perform", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(description, color = TextSec, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SetItem(set: ExerciseSet, onClick: () -> Unit) {
    val bg = if (set.isCompleted)
        Brush.horizontalGradient(listOf(Color(0xFF6650A4), Color(0xFF9C8FD4)))
    else
        Brush.horizontalGradient(listOf(CardBg, CardSoft))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (set.isCompleted) Color.White.copy(alpha = 0.25f) else Primary.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${set.setNumber}",
                    color = if (set.isCompleted) Color.White else Primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Set ${set.setNumber}",
                    color = if (set.isCompleted) Color.White else TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${set.reps} reps  ·  Rest ${set.restSeconds}s",
                    color = if (set.isCompleted) Color.White.copy(alpha = 0.75f) else TextSec,
                    fontSize = 12.sp
                )
            }

            if (set.isCompleted) {
                Box(
                    modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            } else {
                Box(
                    modifier = Modifier.size(32.dp).background(Primary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun TipsSection() {
    val tips = listOf(
        "Keep your back straight throughout the movement.",
        "Breathe in on the way down, out on the way up.",
        "Maintain a consistent pace for best results.",
        "If fatigued, drop to knee push-ups to complete the set."
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pro Tips", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(10.dp))
            tips.forEach { tip ->
                Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                    Box(modifier = Modifier.padding(top = 6.dp).size(6.dp).background(Primary, CircleShape))
                    Spacer(Modifier.width(10.dp))
                    Text(tip, color = TextSec, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}