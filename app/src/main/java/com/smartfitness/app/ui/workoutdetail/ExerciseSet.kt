package com.smartfitness.app.ui.workoutdetail

data class ExerciseSet(
    val setNumber: Int,
    val reps: Int = 12,
    val weightKg: Float = 0f,
    val restSeconds: Int = 60,
    val isCompleted: Boolean = false
)

data class WorkoutDetail(
    val name: String,
    val category: String = "Strength",
    val muscleGroup: String = "Chest",
    val difficulty: String = "Intermediate",
    val durationSeconds: Int,
    val caloriesBurn: Int = 120,
    val sets: List<ExerciseSet>,
    val description: String = "A fundamental upper-body exercise that targets the chest, shoulders, and triceps. Keep your core tight throughout the movement.",
    val videoThumbnailRes: Int? = null
)