package com.example.fittracker.data.repository

import com.example.fittracker.data.local.WorkoutDao

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    suspend fun getTotalWorkouts() = workoutDao.getTotalWorkouts()
    suspend fun getTotalDuration() = workoutDao.getTotalDuration()
    suspend fun getWorkoutFrequency() = workoutDao.getWorkoutFrequency()
    suspend fun getExerciseComparison() = workoutDao.getExerciseComparison()
    suspend fun getRecentWorkouts() = workoutDao.getRecentWorkouts()
}