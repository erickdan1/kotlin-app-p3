package com.example.fittracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.fittracker.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser(): Flow<User>

    @Update
    suspend fun updateUser(user: User)

    // Função para inserir um usuário no banco de dados
    @Insert
    suspend fun insertUser(user: User)

    // Função para buscar um usuário por e-mail e senha
    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?
}