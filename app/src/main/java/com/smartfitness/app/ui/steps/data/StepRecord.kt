package com.smartfitness.app.ui.steps.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_records")
data class StepRecord(
    @PrimaryKey val date: String,   // "yyyy-MM-dd"
    val steps: Int
)

