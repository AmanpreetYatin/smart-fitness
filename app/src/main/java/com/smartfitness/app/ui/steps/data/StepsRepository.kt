package com.smartfitness.app.ui.steps.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepsRepository @Inject constructor(private val dao: StepDao) {
    fun allRecords(): Flow<List<StepRecord>> = dao.getAllRecords()
    suspend fun upsert(record: StepRecord) = dao.upsert(record)
}

