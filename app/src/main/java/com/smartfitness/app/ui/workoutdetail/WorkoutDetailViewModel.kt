package com.smartfitness.app.ui.workoutdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfitness.app.core.database.WorkoutDao
import com.smartfitness.app.core.database.WorkoutEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
   private  val dao: WorkoutDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState

    private var timerJob: Job? = null

    init {
        loadWorkout()
    }

    fun startWorkout() {
        _uiState.update {
            it.copy(
                isWorkoutStarted = true,
                startTime = System.currentTimeMillis()
            )
        }
    }

    fun completeSet(reps: Int) {
        _uiState.update {
            it.copy(
                currentSet = it.currentSet + 1,
                completedSets = it.completedSets + 1,
                totalReps = it.totalReps + reps
            )
        }
    }

    fun finishWorkout() {
        val state = _uiState.value

        val duration = System.currentTimeMillis() - state.startTime

        val workout = WorkoutEntity(
            exerciseName = "Push Ups",
            totalSets = state.completedSets,
            totalReps = state.totalReps,
            duration = duration,
            calories = state.totalReps * 1, // simple logic
            date = System.currentTimeMillis()
        )

        viewModelScope.launch {
            dao.insertWorkout(workout)
        }

        _uiState.value = WorkoutDetailUiState()
    }

    private fun loadWorkout() {
        val workout = WorkoutDetail(
            name = "Push Ups",
            category = "Strength",
            muscleGroup = "Chest · Triceps · Shoulders",
            difficulty = "Intermediate",
            durationSeconds = 30,
            caloriesBurn = 120,
            sets = listOf(
                ExerciseSet(setNumber = 1, reps = 15, weightKg = 0f, restSeconds = 60),
                ExerciseSet(setNumber = 2, reps = 12, weightKg = 0f, restSeconds = 60),
                ExerciseSet(setNumber = 3, reps = 10, weightKg = 0f, restSeconds = 90),
            ),
            description = "A fundamental upper-body exercise that targets the chest, shoulders, and triceps. Keep your core tight and lower your chest to the floor."
        )

        _uiState.update {
            it.copy(workout = workout, currentTime = workout.durationSeconds)
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return

        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }

            while (_uiState.value.currentTime > 0) {
                delay(1000)
                _uiState.update {
                    it.copy(currentTime = it.currentTime - 1)
                }
            }

            _uiState.update { it.copy(isRunning = false) }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        val duration = _uiState.value.workout?.durationSeconds ?: 0
        _uiState.update {
            it.copy(currentTime = duration, isRunning = false)
        }
    }

    fun toggleSetCompletion(setNumber: Int) {
        val workout = _uiState.value.workout ?: return

        val updatedSets = workout.sets.map {
            if (it.setNumber == setNumber) {
                it.copy(isCompleted = !it.isCompleted)
            } else it
        }

        val allDone = updatedSets.all { it.isCompleted }

        _uiState.update {
            it.copy(
                workout = workout.copy(sets = updatedSets),
                isCompleted = allDone
            )
        }
    }
}