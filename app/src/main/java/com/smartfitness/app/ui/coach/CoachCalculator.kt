package com.smartfitness.app.ui.coach

// ===============================
// CoachCalculator.kt
// ===============================

object CoachCalculator {

    fun generatePlan(
        goal: Goal,
        weight: Double,
        height: Double,
        age: Int,
        activity: ActivityLevel
    ): CoachPlan {

        val bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
        val maintenance = bmr * activity.multiplier

        val calories = when (goal) {
            Goal.FAT_LOSS -> maintenance - 400
            Goal.MUSCLE_GAIN -> maintenance + 300
            Goal.MAINTAIN -> maintenance
        }.toInt()

        val protein = (weight * 2).toInt()
        val fats = (weight * 0.8).toInt()
        val carbs = ((calories - (protein * 4) - (fats * 9)) / 4).toInt()
        val water = weight * 0.04

        val workout = when (goal) {
            Goal.FAT_LOSS -> "Push Pull Legs + Cardio"
            Goal.MUSCLE_GAIN -> "Bro Split / PPL Heavy"
            Goal.MAINTAIN -> "Upper Lower Split"
        }

        val weeklyGoal = when (goal) {
            Goal.FAT_LOSS -> "Lose 0.5 kg/week"
            Goal.MUSCLE_GAIN -> "Gain 0.25 kg/week"
            Goal.MAINTAIN -> "Maintain Weight"
        }

        return CoachPlan(
            calories,
            protein,
            carbs,
            fats,
            water,
            workout,
            weeklyGoal
        )
    }
}