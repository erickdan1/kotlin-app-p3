package com.example.fittracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.repository.UserRepository
import com.example.fittracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val repository = WorkoutRepository(workoutDao)
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private var offset = 0
    private val pageSize = 10

    private val _triggerDashboardRefresh = MutableStateFlow(false)
    val triggerDashboardRefresh: StateFlow<Boolean> = _triggerDashboardRefresh

    init {
        loadWorkouts(reset = true)
    }

    fun addWorkout(name: String, type: String, duration: Int) {
        viewModelScope.launch {
            val user = userRepository.getUser() ?: return@launch

            // Estima calorias usando a API Nutritionix com os dados do usuário
            val calories = repository.estimateCaloriesFromWorkout(name, duration, user)

            val newWorkout = Workout(
                date = System.currentTimeMillis(),
                exerciseName = name,
                exerciseType = type,
                duration = duration,
                caloriesBurned = calories
            )

            repository.insertWorkout(newWorkout)

            loadWorkouts(reset = true)
            _triggerDashboardRefresh.value = !_triggerDashboardRefresh.value // mantém atualização da dashboard
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