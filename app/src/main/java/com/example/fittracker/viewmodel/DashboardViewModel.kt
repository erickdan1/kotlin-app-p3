package com.example.fittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.ExerciseComparison
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutFrequency
import com.example.fittracker.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val repository = WorkoutRepository(workoutDao)

    private val _totalWorkouts = MutableLiveData<Int>()
    val totalWorkouts: LiveData<Int> get() = _totalWorkouts

    private val _totalDuration = MutableLiveData<Int>()
    val totalDuration: LiveData<Int> get() = _totalDuration

    private val _workoutFrequency = MutableLiveData<List<WorkoutFrequency>>()
    val workoutFrequency: LiveData<List<WorkoutFrequency>> get() = _workoutFrequency

    private val _exerciseComparison = MutableLiveData<List<ExerciseComparison>>()
    val exerciseComparison: LiveData<List<ExerciseComparison>> get() = _exerciseComparison

    private val _recentWorkouts = MutableLiveData<List<Workout>>()
    val recentWorkouts: LiveData<List<Workout>> get() = _recentWorkouts

    private val _weekNumber = MutableLiveData<Int>()
    val weekNumber: LiveData<Int> get() = _weekNumber

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            _totalWorkouts.postValue(repository.getTotalWorkouts())
            _totalDuration.postValue(repository.getTotalDuration() ?: 0)
            _workoutFrequency.postValue(repository.getWorkoutFrequency())
            _exerciseComparison.postValue(repository.getExerciseComparison())
            _recentWorkouts.postValue(repository.getRecentWorkouts())
            _weekNumber.postValue(1) // Pode ser calculado dinamicamente
        }
    }
}