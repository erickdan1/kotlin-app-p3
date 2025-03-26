package com.example.fittracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.local.AppDatabase
import com.example.fittracker.data.model.User
import com.example.fittracker.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository
    private val _userLiveData = MutableLiveData<User?>() // Mutable para ser alterado
    val userLiveData: LiveData<User?> get() = _userLiveData // Apenas leitura para a Activity

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    // Cadastro do usuário
    fun registerUser(user: User) = viewModelScope.launch {
        repository.registerUser(user)
    }

    // Login usando coroutines e postando no LiveData
    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.login(email, password)
            _userLiveData.postValue(user) // Postando o valor na thread principal
        }
    }
}