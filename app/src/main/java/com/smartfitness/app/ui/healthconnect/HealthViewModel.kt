package com.smartfitness.app.ui.healthconnect

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfitness.app.core.utilities.rememberPermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    private val _state = MutableStateFlow(HealthUiState())
    val state: StateFlow<HealthUiState> = _state



    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSteps(manager: HealthConnectManager) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)

            val steps = manager.getTodaySteps()

            _state.value = HealthUiState(
                steps = steps,
                loading = false
            )
        }
    }
}