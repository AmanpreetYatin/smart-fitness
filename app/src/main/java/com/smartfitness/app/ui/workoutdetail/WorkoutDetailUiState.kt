package com.smartfitness.app.ui.workoutdetail

data class WorkoutDetailUiState(
    val workout: WorkoutDetail? = null,
    val currentTime: Int = 0,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false,
    val isWorkoutStarted: Boolean = false,
    val currentSet: Int = 1,
    val completedSets: Int = 0,
    val totalReps: Int = 0,
    val startTime: Long = 0L,
    val elapsedTime: Long = 0L
)