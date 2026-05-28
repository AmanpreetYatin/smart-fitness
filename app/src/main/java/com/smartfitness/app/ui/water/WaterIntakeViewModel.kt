package com.smartfitness.app.ui.water

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WaterIntakeViewModel @Inject constructor() : ViewModel() {

    private val _consumed = MutableStateFlow(2100)
    val consumed: StateFlow<Int> = _consumed

    val goal = 3000

    private val _logs = MutableStateFlow(
        listOf(
            WaterLog("07:00 AM", 250),
            WaterLog("09:30 AM", 300),
            WaterLog("12:00 PM", 500),
            WaterLog("03:00 PM", 250),
            WaterLog("06:00 PM", 800),
        )
    )
    val logs: StateFlow<List<WaterLog>> = _logs

    fun addWater(amount: Int) {
        _consumed.update { it + amount }
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        _logs.update { listOf(WaterLog(time, amount)) + it }
    }

    fun removeWater(amount: Int) {
        _consumed.update { (it - amount).coerceAtLeast(0) }
    }
}

