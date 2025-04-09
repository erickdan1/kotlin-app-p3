package com.example.fittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.Achievement
import com.example.fittracker.data.repository.AchievementRepository
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val achievementDao = AppDatabase.getDatabase(application).achievementDao()
    private val repository = AchievementRepository(achievementDao)

    private val _recentAchievements = MutableLiveData<List<Achievement>>()
    val recentAchievements: LiveData<List<Achievement>> get() = _recentAchievements

    init {
        loadRecentAchievements()
    }

    fun loadRecentAchievements() {
        viewModelScope.launch {
            _recentAchievements.postValue(repository.getRecentAchievements())
        }
    }
}