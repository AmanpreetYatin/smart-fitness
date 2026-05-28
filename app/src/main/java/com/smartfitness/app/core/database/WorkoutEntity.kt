package com.smartfitness.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_history")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseName: String,
    val totalSets: Int,
    val totalReps: Int,
    val duration: Long,
    val calories: Int,
    val date: Long
)