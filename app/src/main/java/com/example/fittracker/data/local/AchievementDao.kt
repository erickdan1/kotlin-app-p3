package com.example.fittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.model.Achievement

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement ORDER BY date DESC LIMIT 3")
    suspend fun getRecentAchievements(): List<Achievement>

    // Se desejar uma função para obter todas as conquistas:
    @Query("SELECT * FROM achievement ORDER BY date DESC")
    suspend fun getAllAchievements(): List<Achievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievement WHERE id = :id")
    suspend fun getAchievementById(id: Int): Achievement?
}