package com.example.fittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.model.ExerciseComparison
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutFrequency

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout)

    @Query("SELECT * FROM workout ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getWorkoutsPaged(limit: Int, offset: Int): List<Workout>

    // Retorna os 5 treinos mais recentes
    @Query("SELECT * FROM workout ORDER BY date DESC LIMIT 5")
    suspend fun getRecentWorkouts(): List<Workout>

    // Total de treinos
    @Query("SELECT COUNT(*) FROM workout")
    suspend fun getTotalWorkouts(): Int

    // Tempo total de exercícios (soma das durações)
    @Query("SELECT SUM(duration) FROM workout")
    suspend fun getTotalDuration(): Int?

    // Dados para gráfico de linha: frequência de treinos por dia (exemplo simples)
    @Query("SELECT date, COUNT(*) as count FROM workout GROUP BY date")
    suspend fun getWorkoutFrequency(): List<WorkoutFrequency>

    // Dados para gráfico de barras: contagem de cada exercício praticado
    @Query("SELECT exerciseName, COUNT(*) as count FROM workout GROUP BY exerciseName")
    suspend fun getExerciseComparison(): List<ExerciseComparison>

    // Soma de calorias queimadas
    @Query("SELECT SUM(caloriesBurned) FROM workout")
    suspend fun getTotalCaloriesBurned(): Float?
}