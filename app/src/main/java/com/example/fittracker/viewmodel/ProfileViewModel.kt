package com.example.fittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.Achievement
import com.example.fittracker.data.model.User
import com.example.fittracker.data.repository.AchievementRepository
import kotlinx.coroutines.launch
import com.example.fittracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val achievementDao = AppDatabase.getDatabase(application).achievementDao()
    private val repository = UserRepository(userDao)
    private val achievementRepository = AchievementRepository(achievementDao)

    // Estados para os dados do usuário
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> get() = _achievements

    // Trigger para forçar a recomposição da UI
    private val _triggerProfileRefresh = MutableStateFlow(false)
    val triggerProfileRefresh: StateFlow<Boolean> = _triggerProfileRefresh

    init {
        loadUser()
        loadAchievements()
    }

    fun loadUser() {
        viewModelScope.launch {
            _user.postValue(repository.getUser())
        }
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _achievements.postValue(achievementRepository.getRecentAchievements())
        }
    }

    fun updateUserProfile(
        newName: String? = null,
        newAge: Int? = null,
        newWeight: Float? = null,
        newHeight: Float? = null
    ) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = newName ?: currentUser.name,
                    age = newAge ?: currentUser.age,
                    weight = newWeight ?: currentUser.weight,
                    height = newHeight ?: currentUser.height
                )
                repository.updateUser(updatedUser)
                _user.postValue(updatedUser)
                // Alterna o trigger para forçar recomposição
                _triggerProfileRefresh.value = !_triggerProfileRefresh.value
            }
        }
    }
}