package com.smartfitness.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfitness.app.core.database.WorkoutDao
import com.smartfitness.app.core.database.WorkoutEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState

    init {
        loadExercises()
    }


    private fun loadExercises() {
        val dummyList = listOf(
            Exercise(1, "Push Ups", "Chest", "10 reps", ""),
            Exercise(2, "Squats", "Legs", "15 reps", ""),
            Exercise(3, "Plank", "Core", "30 sec", ""),
            Exercise(4, "Jumping Jacks", "Full Body", "20 reps", "")
        )

        _uiState.update {
            it.copy(exercises = dummyList)
        }
    }
}