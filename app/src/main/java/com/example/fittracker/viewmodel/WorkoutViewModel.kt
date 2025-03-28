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
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val repository = WorkoutRepository(workoutDao)

    private val _workouts = MutableLiveData<List<Workout>>()
    val workouts: LiveData<List<Workout>> get() = _workouts

    private var offset = 0
    private val pageSize = 10

    init {
        loadWorkouts()
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
            // Verificar se a inserção foi bem-sucedida
            val workoutsAfterInsert = repository.getWorkoutsPaged(pageSize, 0) // Carregar desde o início
            Log.d("WorkoutViewModel", "Treinos após inserção: $workoutsAfterInsert")
            loadWorkouts()
        }
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            // Resetar o offset quando precisar recarregar a lista do zero
            if (offset == 0) {
                val newWorkouts = repository.getWorkoutsPaged(pageSize, offset)
                _workouts.postValue(newWorkouts)
            } else {
                val newWorkouts = repository.getWorkoutsPaged(pageSize, offset)
                _workouts.postValue((_workouts.value ?: emptyList()) + newWorkouts)
            }
            offset += pageSize
        }
    }
}