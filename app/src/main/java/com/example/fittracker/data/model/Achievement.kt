package com.example.fittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fittracker.data.repository.AchievementRepository
import com.example.fittracker.data.repository.WorkoutRepository

@Entity(tableName = "achievement")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: Long, // Timestamp da conquista
    val iconUrl: String? = null // Para carregar imagem externa; ou pode ser nulo para usar ícone padrão
)

data class AchievementCriteria(
    val id: Int,
    val title: String,
    val description: String,
    val minWorkouts: Int,
    val minDuration: Int, // em minutos
    val iconResName: String
)