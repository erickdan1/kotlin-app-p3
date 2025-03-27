package com.example.fittracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittracker.ui.screens.*
import com.example.fittracker.viewmodel.DashboardViewModel
import com.example.fittracker.viewmodel.UserViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: UserViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("home") { HomeScreen(navController, viewModel, dashboardViewModel) }
    }
}