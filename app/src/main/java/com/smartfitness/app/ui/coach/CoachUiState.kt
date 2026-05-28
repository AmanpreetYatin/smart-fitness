package com.smartfitness.app.ui.coach

data class CoachUiState(
    val selectedGoal: Goal = Goal.FAT_LOSS,
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.BEGINNER,
    val result: CoachPlan? = null,
    val isLoading: Boolean = false,
    val weightError: String? = null,
    val heightError: String? = null,
    val ageError: String? = null
)

enum class Goal(val title: String) {
    FAT_LOSS("Fat Loss"),
    MUSCLE_GAIN("Muscle Gain"),
    MAINTAIN("Maintain")
}

enum class ActivityLevel(val multiplier: Double) {
    BEGINNER(1.2),
    MODERATE(1.45),
    ACTIVE(1.7)
}

data class CoachPlan(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val water: Double,
    val workout: String,
    val weeklyGoal: String
)