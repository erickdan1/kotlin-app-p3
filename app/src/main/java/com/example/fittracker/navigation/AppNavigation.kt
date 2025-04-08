package com.example.fittracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittracker.ui.screens.*
import com.example.fittracker.viewmodel.DashboardViewModel
import com.example.fittracker.viewmodel.OnboardingViewModel
import com.example.fittracker.viewmodel.ProfileViewModel
import com.example.fittracker.viewmodel.UserViewModel
import com.example.fittracker.viewmodel.WorkoutViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: UserViewModel = viewModel()
    val dashboardViewModel: DashboardViewModel = viewModel()
    val workoutViewModel: WorkoutViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val onboardingViewModel: OnboardingViewModel = viewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("register") { RegisterScreen(navController, viewModel) }
        composable("onboarding") { OnboardingScreen(navController, onboardingViewModel) }
        composable("home") { HomeScreen(navController, viewModel, workoutViewModel, dashboardViewModel) }
        composable("atividades") { WorkoutScreen(workoutViewModel, navController) }
        composable("perfil") { ProfileScreen(navController, profileViewModel) }
    }
}