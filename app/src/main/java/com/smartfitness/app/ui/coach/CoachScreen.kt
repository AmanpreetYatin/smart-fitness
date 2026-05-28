package com.smartfitness.app.ui.coach

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartfitness.app.core.ui.components.SmartFitnessTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachScreen(
    vm: CoachViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            SmartFitnessTopBar(
                title = "Your AI Coach",
                showBackButton = false,
                onBackClick = onBack
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        )
        {

            item {
                Text("Get your custom fitness plan")
            }

            item {
                Text("Select Goal")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Goal.values().forEach { goal ->
                        FilterChip(
                            selected = state.selectedGoal == goal,
                            onClick = { vm.updateGoal(goal) },
                            label = { Text(goal.title) }
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = state.weight,
                    onValueChange = vm::updateWeight,
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.weightError != null,
                    supportingText = state.weightError?.let { { Text(it) } }
                )
            }

            item {
                OutlinedTextField(
                    value = state.height,
                    onValueChange = vm::updateHeight,
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.heightError != null,
                    supportingText = state.heightError?.let { { Text(it) } }
                )
            }

            item {
                OutlinedTextField(
                    value = state.age,
                    onValueChange = vm::updateAge,
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.ageError != null,
                    supportingText = state.ageError?.let { { Text(it) } }
                )
            }

            item {
                Text("Activity Level")

                Column {
                    ActivityLevel.values().forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.activityLevel == level,
                                    onClick = { vm.updateActivity(level) }
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = state.activityLevel == level,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(level.name)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { vm.generatePlan() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Plan")
                }
            }

            if (state.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            state.result?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Daily Calories: ${result.calories}")
                            Text("Protein: ${result.protein} g")
                            Text("Carbs: ${result.carbs} g")
                            Text("Fats: ${result.fats} g")
                            Text("Water: ${"%.1f".format(result.water)} L")
                            Text("Workout: ${result.workout}")
                            Text("Weekly Goal: ${result.weeklyGoal}")
                        }
                    }
                }
            }
        }
    }
    }
}



