package com.example.fittracker.data.repository

import com.example.fittracker.data.local.AchievementDao
import com.example.fittracker.data.model.Achievement

class AchievementRepository(private val achievementDao: AchievementDao) {
    suspend fun getRecentAchievements() = achievementDao.getRecentAchievements()
    suspend fun getAllAchievements() = achievementDao.getAllAchievements()
    suspend fun insertAchievement(achievement: Achievement) = achievementDao.insertAchievement(achievement)
}