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

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.postValue(repository.getUser())
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _achievements.postValue(achievementRepository.getRecentAchievements())
        }
    }

    // Atualiza apenas o nome
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(name = newName)
                repository.updateUser(updatedUser)
                _user.postValue(updatedUser)
            }
        }
    }

    // Atualiza idade, peso e altura
    fun updateUserDetails(newAge: Int, newWeight: Float, newHeight: Float) {
        viewModelScope.launch {
            val currentUser = _user.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(age = newAge, weight = newWeight, height = newHeight)
                repository.updateUser(updatedUser)
                _user.postValue(updatedUser)
            }
        }
    }
}