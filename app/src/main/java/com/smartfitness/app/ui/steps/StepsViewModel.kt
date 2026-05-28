package com.smartfitness.app.ui.steps

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfitness.app.ui.steps.data.StepRecord
import com.smartfitness.app.ui.steps.data.StepsRepository
import com.smartfitness.app.ui.steps.service.StepCounterService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StepsViewModel @Inject constructor(
    private val repository: StepsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val goal = 10000

    // All historical records from Room DB
    val records: StateFlow<List<StepRecord>> = repository.allRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live today steps — polled from the service's in-memory counter
    private val _todaySteps = MutableStateFlow(StepCounterService.todaySteps)
    val todaySteps: StateFlow<Int> = _todaySteps

    init {
        // Poll service's todaySteps every second for live UI update
        viewModelScope.launch {
            while (true) {
                _todaySteps.value = StepCounterService.todaySteps
                delay(1000)
            }
        }
    }

    fun startService() {
        val intent = Intent(context, StepCounterService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopService() {
        val intent = Intent(context, StepCounterService::class.java).apply {
            action = StepCounterService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun todayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}


