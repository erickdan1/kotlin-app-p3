package com.example.fittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int = 1, // Apenas um usuário no app
    val name: String,
    val email: String,
    val password: String,
    val profilePicture: String? = null, // URL da foto de perfil
    val goal: Int? = null // Meta de treinos
)