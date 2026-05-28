package com.smartfitness.app.ui.steps.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM step_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<StepRecord>>

    @Query("SELECT * FROM step_records WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): StepRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: StepRecord)
}

