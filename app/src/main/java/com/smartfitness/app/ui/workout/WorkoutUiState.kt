package com.smartfitness.app.ui.workout

data class WorkoutUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false
)