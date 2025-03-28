package com.example.fittracker.data.repository

import com.example.fittracker.data.local.WorkoutDao
import com.example.fittracker.data.model.Workout

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    suspend fun insertWorkout(workout: Workout) = workoutDao.insertWorkout(workout)
    suspend fun getWorkoutsPaged(limit: Int, offset: Int) = workoutDao.getWorkoutsPaged(limit, offset)
    suspend fun getTotalWorkouts() = workoutDao.getTotalWorkouts()
    suspend fun getTotalDuration() = workoutDao.getTotalDuration()
    suspend fun getWorkoutFrequency() = workoutDao.getWorkoutFrequency()
    suspend fun getExerciseComparison() = workoutDao.getExerciseComparison()
    suspend fun getRecentWorkouts() = workoutDao.getRecentWorkouts()
}