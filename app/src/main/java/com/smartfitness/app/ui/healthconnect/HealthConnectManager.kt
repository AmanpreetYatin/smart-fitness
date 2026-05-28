package com.smartfitness.app.ui.healthconnect

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit

class HealthConnectManager(
    private val context: Context
) {

    private val client = HealthConnectClient.getOrCreate(context)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTodaySteps(): Long {
        val start = Instant.now().truncatedTo(ChronoUnit.DAYS)
        val end = Instant.now()

        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(start, end)
            )
        )

        return response.records.sumOf { it.count }
    }
}