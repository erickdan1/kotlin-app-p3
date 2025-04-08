package com.example.fittracker.data.model

data class NutritionixRequest(
    val query: String, // exemplo: "ran 30 minutes"
    val gender: String = "male", // ou "female"
    val weight_kg: Float,
    val height_cm: Float,
    val age: Int
)

data class NutritionixResponse(
    val exercises: List<Exercise>
)

data class Exercise(
    val name: String,
    val duration_min: Float,
    val nf_calories: Float
)