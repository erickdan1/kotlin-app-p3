package com.example.fittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,          // Timestamp do treino
    val duration: Int,       // Duração em minutos
    val exerciseType: String // Tipo ou nome do exercício
)

data class WorkoutFrequency(
    val date: Long,
    val count: Int
)

data class ExerciseComparison(
    val exerciseType: String,
    val count: Int
)