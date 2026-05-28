package com.smartfitness.app.ui.steps.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StepRecord::class], version = 1, exportSchema = false)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
}

