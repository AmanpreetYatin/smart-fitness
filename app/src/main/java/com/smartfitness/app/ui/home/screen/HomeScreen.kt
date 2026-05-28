package com.smartfitness.app.ui.home.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.ui.water.WaterIntakeViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfitness.app.core.theme.Background
import com.smartfitness.app.core.theme.BrandColor
import com.smartfitness.app.core.theme.CardColor
import com.smartfitness.app.core.theme.CardSoft
import com.smartfitness.app.core.theme.LightPurple
import com.smartfitness.app.core.theme.TextPrimary
import com.smartfitness.app.core.theme.TextSecondary




@Composable
fun HomeDashboardScreen(
    userName: String = "Aman",
    onWorkoutClick: () -> Unit = {},
    onWaterClick: () -> Unit = {},
    onStepsClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onHealthSync: () -> Unit = {}
) {
    val activity = LocalActivity.current as ComponentActivity
    val waterViewModel: WaterIntakeViewModel = hiltViewModel(activity)
    val scrollState = rememberScrollState()
    val waterConsumed by waterViewModel.consumed.collectAsState()

    Scaffold(
        containerColor = Background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Surface(
                color = Background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                  //  Spacer(modifier = Modifier.statusBarsPadding())
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Good Morning 👋", color = TextSecondary, fontSize = 14.sp)
                            Text(text = userName, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { /* TODO: open settings */ },
                            modifier = Modifier
                                .size(44.dp)
                                .background(BrandColor.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = BrandColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Stats Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(250.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {

            item { DashboardCard("Calories", "540 kcal", Color(0xFFEF4444 ) )  }
            item { DashboardCard("Water", "$waterConsumed ml", Color(0xFF0288D1) , onClick = onWaterClick) }
            item { DashboardCard("Health Sync", "", Color(0xFFF59E0B), onClick =  onHealthSync) }
            item { DashboardCard("Steps", "8,420", Color(0xFF4CAF50), onClick = onStepsClick) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Workout
        WorkoutPlanCard(onWorkoutClick)

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Actions
        Text(text = "Quick Actions", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickButton("💧 Water", onWaterClick)
            QuickButton("📈 Progress", onProgressClick)
            QuickButton("🔔 Alerts", onNotificationClick)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Motivation Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightPurple),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Stay consistent. Results take time 🔥",
                color = BrandColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, color: Color, onClick: (() -> Unit)? = null) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = title, color = TextSecondary, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WorkoutPlanCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(BrandColor, Color(0xFF9C8FD4))),
                    RoundedCornerShape(24.dp)
                )
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(text = "Today's Workout", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Chest & Triceps", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "Start", color = BrandColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun QuickButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CardSoft)
    ) {
        Text(text = title, color = BrandColor, fontWeight = FontWeight.Medium)
    }
}