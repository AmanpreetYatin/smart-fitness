package com.smartfitness.app.ui.coach

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoachViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<CoachUiState> = MutableStateFlow(CoachUiState())

    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    fun updateWeight(value: String) {
        _uiState.update { it.copy(weight = value, weightError = null) }
    }

    fun updateHeight(value: String) {
        _uiState.update { it.copy(height = value, heightError = null) }
    }

    fun updateAge(value: String) {
        _uiState.update { it.copy(age = value, ageError = null) }
    }

    fun updateGoal(goal: Goal) {
        _uiState.update { it.copy(selectedGoal = goal) }
    }

    fun updateActivity(level: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = level) }
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val weightValid = state.weight.isNotBlank() && state.weight.toDoubleOrNull() != null
        val heightValid = state.height.isNotBlank() && state.height.toDoubleOrNull() != null
        val ageValid = state.age.isNotBlank() && state.age.toIntOrNull() != null

        _uiState.update {
            it.copy(
                weightError = if (state.weight.isBlank()) "Weight is required" else if (state.weight.toDoubleOrNull() == null) "Invalid weight" else null,
                heightError = if (state.height.isBlank()) "Height is required" else if (state.height.toDoubleOrNull() == null) "Invalid height" else null,
                ageError = if (state.age.isBlank()) "Age is required" else if (state.age.toIntOrNull() == null) "Invalid age" else null
            )
        }

        return weightValid && heightValid && ageValid
    }

    fun generatePlan() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1200)

            val state = _uiState.value

            try {
                val result = CoachCalculator.generatePlan(
                    goal = state.selectedGoal,
                    weight = state.weight.toDouble(),
                    height = state.height.toDouble(),
                    age = state.age.toInt(),
                    activity = state.activityLevel
                )

                _uiState.update {
                    it.copy(
                        result = result,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


}