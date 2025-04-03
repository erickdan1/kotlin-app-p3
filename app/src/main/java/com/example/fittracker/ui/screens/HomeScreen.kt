package com.example.fittracker.ui.screens

import android.util.Log
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.fittracker.data.model.ExerciseComparison
import com.example.fittracker.data.model.Workout
import com.example.fittracker.data.model.WorkoutFrequency
import com.example.fittracker.viewmodel.DashboardViewModel
import com.example.fittracker.viewmodel.UserViewModel
import com.example.fittracker.viewmodel.WorkoutViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    workoutViewModel: WorkoutViewModel,
    dashboardViewModel: DashboardViewModel
) {
    // Observando os dados dos ViewModels
    val user by userViewModel.userLiveData.observeAsState()
    val totalWorkouts by dashboardViewModel.totalWorkouts.observeAsState(0)
    val totalDuration by dashboardViewModel.totalDuration.observeAsState(0)
    val weekNumber by dashboardViewModel.weekNumber.observeAsState(1)
    val workoutFrequency by dashboardViewModel.workoutFrequency.observeAsState(emptyList())
    val exerciseComparison by dashboardViewModel.exerciseComparison.observeAsState(emptyList())
    val recentWorkouts by dashboardViewModel.recentWorkouts.observeAsState(emptyList())

    // Observa a lista de treinos e demais dados do dashboard
    val workouts by workoutViewModel.workouts.observeAsState(emptyList())

    // Coleta o evento de novo treino e atualiza o dashboard
    LaunchedEffect(Unit) {
        workoutViewModel.workoutAddedEvent.collect {
            Log.d("HomeScreen", "Novo treino detectado! Atualizando dashboard...")
            dashboardViewModel.refreshData()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Saudação personalizada
                Text(
                    text = "Bem-vindo, ${user?.name ?: "Usuário"}!",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            item {
                // Semana X
                Text(
                    text = "Semana $weekNumber",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            item {
                // Gráfico de Linha: Frequência de Treinos
                Text(
                    text = "Resumo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            item {
                // Cards para Total de Treinos e Tempo Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCard(title = "Total de treinos", value = totalWorkouts.toString())
                    InfoCard(title = "Tempo total (min)", value = "$totalDuration")
                }
            }
            item {
                // Gráfico de Linha: Frequência de Treinos
                Text(
                    text = "Frequência de Treinos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            item { WorkoutFrequencyChart(workoutFrequency) }
            item {
                // Gráfico de Barras: Comparação de Exercícios
                Text(
                    text = "Comparação de Exercícios",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            item { ExerciseComparisonChart(exerciseComparison) }
            item {
                // Lista de Atividades Recentes
                Text(
                    text = "Atividades Recentes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            items(recentWorkouts) { workout ->
                WorkoutItem(workout)
            }
        }
    }
}

@Composable
fun WorkoutFrequencyChart(workoutFrequency: List<WorkoutFrequency>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                description = Description().apply { text = "Frequência de Treinos" }
                setTouchEnabled(true)
                setPinchZoom(true)
            }
        },
        update = { lineChart ->
            val entries = workoutFrequency.mapIndexed { index, wf ->
                Entry(index.toFloat(), wf.count.toFloat())
            }
            val dataSet = LineDataSet(entries, "Treinos").apply {
                color = android.graphics.Color.BLUE
                valueTextColor = android.graphics.Color.BLACK
                lineWidth = 2f
                circleRadius = 4f
                setDrawCircles(true)
                setDrawValues(true)
            }
            lineChart.data = LineData(dataSet)
            lineChart.invalidate()
        }
    )
}

@Composable
fun ExerciseComparisonChart(exerciseComparison: List<ExerciseComparison>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            BarChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                description = Description().apply { text = "Comparação de Exercícios" }
                setDrawValueAboveBar(true)
            }
        },
        update = { barChart ->
            val entries = exerciseComparison.mapIndexed { index, ec ->
                BarEntry(index.toFloat(), ec.count.toFloat())
            }
            val dataSet = BarDataSet(entries, "Exercícios").apply {
                color = android.graphics.Color.GREEN
                valueTextColor = android.graphics.Color.BLACK
            }
            barChart.data = BarData(dataSet)
            barChart.invalidate()
        }
    )
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        "home" to Icons.Filled.Home,
        "atividades" to Icons.AutoMirrored.Filled.List,
        "perfil" to Icons.Filled.Person
    )
    NavigationBar {
        items.forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route.replaceFirstChar { it.uppercase() }) },
                selected = false,
                onClick = { navController.navigate(route) }
            )
        }
    }
}