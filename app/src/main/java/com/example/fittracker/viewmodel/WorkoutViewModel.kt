package com.example.fittracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.Achievement
import com.example.fittracker.data.model.AchievementCriteria
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.repository.AchievementRepository
import com.example.fittracker.data.repository.UserRepository
import com.example.fittracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val workoutDao = AppDatabase.getDatabase(application).workoutDao()
    private val repository = WorkoutRepository(workoutDao)
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)
    private val achievementDao = AppDatabase.getDatabase(application).achievementDao()
    private val achievementRepository = AchievementRepository(achievementDao)

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
            // Estima calorias usando a API Nutritionix com os dados do usuário
            // val calories = repository.estimateCaloriesFromWorkout(name, duration, user)

            val newWorkout = Workout(
                date = System.currentTimeMillis(),
                exerciseName = name,
                exerciseType = type,
                duration = duration,
                // caloriesBurned = calories
            )

            repository.insertWorkout(newWorkout)

            loadWorkouts(reset = true)
            checkAchievements()
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

    private suspend fun checkAchievements() {
        // Obtenha os critérios atuais com base nos dados do repositório
        val totalWorkouts = repository.getTotalWorkouts()
        val totalDuration = repository.getTotalDuration() ?: 0

        // Defina os critérios para cada conquista
        val criteriaList = listOf(
            AchievementCriteria(1, "Aí você tá demonstrando a essência",
                "Você completou pelo menos 5 treinos e 60 minutos no total.", 5, 60),
            AchievementCriteria(2, "Eu quero eu posso",
                "Você completou pelo menos 10 treinos e 120 minutos no total.", 10, 120),
            AchievementCriteria(3, "Tá saindo da jaula o monstro",
                "Você completou pelo menos 20 treinos e 300 minutos no total.", 20, 300),
            AchievementCriteria(4, "Yeah buddy!!",
                "Você completou pelo menos 30 treinos e 420 minutos no total.", 30, 420),
            AchievementCriteria(5, "Se quiser sim mano",
                "Você completou pelo menos 50 treinos e 600 minutos no total.", 50, 600),
            AchievementCriteria(6, "Birl!!!",
                "Você completou pelo menos 100 treinos e 1000 minutos no total.", 100, 1000)
        )

        // Verifique cada critério e, se atingido, insira a conquista (se ainda não estiver desbloqueada)
        for (criteria in criteriaList) {
            if (totalWorkouts >= criteria.minWorkouts && totalDuration >= criteria.minDuration) {
                if (!achievementRepository.isAchievementUnlocked(criteria.id)) {
                    val achievement = Achievement(
                        id = criteria.id,
                        title = criteria.title,
                        description = criteria.description,
                        date = System.currentTimeMillis(),
                        iconUrl = null
                    )
                    achievementRepository.insertAchievement(achievement)
                }
            }
        }
    }
}