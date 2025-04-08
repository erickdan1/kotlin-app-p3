package com.example.fittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.User
import kotlinx.coroutines.launch

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    init {
        viewModelScope.launch {
            _user.postValue(userDao.getUser())
        }
    }

    fun saveUserData(age: Int, weight: Float, height: Float) {
        viewModelScope.launch {
            val existingUser = userDao.getUser()
            if (existingUser != null) {
                val updatedUser = existingUser.copy(age = age, weight = weight, height = height)
                userDao.updateUser(updatedUser)
            } else {
                val newUser = User(
                    id = 1,
                    name = "", // Pode ser atualizado depois
                    email = "",
                    password = "",
                    age = age,
                    weight = weight,
                    height = height
                )
                userDao.insertUser(newUser)
            }
            _user.postValue(userDao.getUser())
        }
    }
}