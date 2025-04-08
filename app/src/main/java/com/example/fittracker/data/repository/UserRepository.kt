package com.example.fittracker.data.repository

import com.example.fittracker.data.local.UserDao
import com.example.fittracker.data.model.User

class UserRepository(private val userDao: UserDao) {
    // Função para cadastro
    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    // Função para login
    suspend fun login(email: String, password: String): User? {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    suspend fun getUser() = userDao.getUser()
    suspend fun updateUser(user: User) = userDao.updateUser(user)
}