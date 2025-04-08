package com.example.fittracker.data.repository

import com.example.fittracker.data.local.WorkoutDao
import com.example.fittracker.data.model.NutritionixRequest
import com.example.fittracker.data.model.User
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.remote.RetrofitInstance

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    suspend fun insertWorkout(workout: Workout) = workoutDao.insertWorkout(workout)
    suspend fun getWorkoutsPaged(limit: Int, offset: Int) = workoutDao.getWorkoutsPaged(limit, offset)
    suspend fun getTotalWorkouts() = workoutDao.getTotalWorkouts()
    suspend fun getTotalDuration() = workoutDao.getTotalDuration()
    suspend fun getWorkoutFrequency() = workoutDao.getWorkoutFrequency()
    suspend fun getExerciseComparison() = workoutDao.getExerciseComparison()
    suspend fun getRecentWorkouts() = workoutDao.getRecentWorkouts()

    suspend fun estimateCaloriesFromWorkout(
        name: String,
        duration: Int,
        user: User
    ): Float {
        val request = NutritionixRequest(
            query = "$name for $duration minutes",
            weight_kg = user.weight ?: 70f,
            height_cm = user.height ?: 170f,
            age = user.age ?: 25
        )

        return try {
            val response = RetrofitInstance.api.estimateCalories(request)
            response.exercises.firstOrNull()?.nf_calories ?: 0f
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    suspend fun getTotalCaloriesBurned(): Float? {
        return workoutDao.getTotalCaloriesBurned()
    }
}