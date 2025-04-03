package com.example.fittracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val repository = WorkoutRepository(workoutDao)

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private var offset = 0
    private val pageSize = 10

    // Evento para notificar que um novo treino foi adicionado
    private val _workoutAddedEvent = MutableSharedFlow<Unit>(replay = 0)
    val workoutAddedEvent = _workoutAddedEvent.asSharedFlow()

    init {
        loadWorkouts(reset = true)
    }

    fun addWorkout(name: String, type: String, duration: Int) {
        viewModelScope.launch {
            val newWorkout = Workout(
                date = System.currentTimeMillis(),
                exerciseName = name,
                exerciseType = type,
                duration = duration
            )
            repository.insertWorkout(newWorkout)
            loadWorkouts(reset = true)
            _workoutAddedEvent.emit(Unit) // Emite o evento para notificar que um treino foi adicionado
        }
    }

    fun loadWorkouts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                offset = 0
                val newWorkouts = repository.getWorkoutsPaged(pageSize, offset)
                _workouts.postValue(newWorkouts)
                offset = pageSize
            } else {
                val newWorkouts = repository.getWorkoutsPaged(pageSize, offset)
                _workouts.postValue((_workouts.value.orEmpty()) + newWorkouts)
                offset += pageSize
            }
        }
    }
}