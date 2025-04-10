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
import androidx.compose.runtime.collectAsState
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Calendar

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

    val trigger by workoutViewModel.triggerDashboardRefresh.collectAsState()

    LaunchedEffect(trigger) {
        Log.d("HomeScreen", "Novo treino detectado! Atualizando dashboard...")
        dashboardViewModel.refreshData()
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
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(title = "Total de treinos", value = totalWorkouts.toString())
                        InfoCard(title = "Tempo total (min)", value = "$totalDuration")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val totalCalories by dashboardViewModel.totalCalories.observeAsState(0f)
                        InfoCard(title = "Calorias queimadas", value = "${totalCalories.toInt()} kcal")
                    }
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
    // Lista fixa com os rótulos dos dias da semana
    val daysOfWeek = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

    // Inicializa uma lista de 7 elementos (0 a 6) com o valor 0
    val frequencyByIndex = MutableList(7) { 0 }

    // Itera sobre a lista de WorkoutFrequency e agrupa os counts pelo dia da semana
    for (wf in workoutFrequency) {
        // Converte o timestamp para dia da semana usando Calendar
        val calendar = Calendar.getInstance().apply {
            timeInMillis = wf.date
        }
        // Calendar.DAY_OF_WEEK retorna valores de 1 (Domingo) a 7 (Sábado)
        val dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // agora 0 a 6
        // Acumula o count para este dia (soma caso já exista outro registro para o mesmo dia)
        frequencyByIndex[dayIndex] = frequencyByIndex[dayIndex] + wf.count
    }

    // Cria uma lista de Entry para o gráfico:
    // x = índice do dia, y = count do dia
    val entries = frequencyByIndex.mapIndexed { index, count ->
        Entry(index.toFloat(), count.toFloat())
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            // Cria o LineChart
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                // Configurações do gráfico
                description = Description().apply { text = "Frequência de Treinos por Dia" }
                setTouchEnabled(true)
                setPinchZoom(true)
                axisRight.isEnabled = false
                // Configura o eixo X para mostrar os dias da semana
                xAxis.apply {
                    granularity = 1f
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = IndexAxisValueFormatter(daysOfWeek)
                    labelRotationAngle = -45f
                }
                // Garante que o eixo Y comece em zero
                axisLeft.axisMinimum = 0f
            }
        },
        update = { lineChart ->
            val dataSet = LineDataSet(entries, "Treinos").apply {
                color = android.graphics.Color.BLUE
                valueTextColor = android.graphics.Color.BLACK
                lineWidth = 2f
                circleRadius = 6f
                setDrawCircles(true)
                setDrawValues(true)
                setCircleColor(android.graphics.Color.BLUE)
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
                axisRight.isEnabled = false
            }
        },
        update = { barChart ->
            // Cria as entradas para o gráfico usando o índice como valor X e o count como valor Y
            val entries = exerciseComparison.mapIndexed { index, ec ->
                BarEntry(index.toFloat(), ec.count.toFloat())
            }

            // Cria um BarDataSet e atribui cores diferentes a cada barra
            val dataSet = BarDataSet(entries, "Exercícios").apply {
                // Lista de cores pré-definida
                val predefinedColors = listOf(
                    android.graphics.Color.RED,
                    android.graphics.Color.GREEN,
                    android.graphics.Color.BLUE,
                    android.graphics.Color.MAGENTA,
                    android.graphics.Color.CYAN,
                    android.graphics.Color.YELLOW,
                    android.graphics.Color.LTGRAY
                )
                // Atribui, para cada entrada, uma cor da lista (ciclando se necessário)
                val colors = exerciseComparison.mapIndexed { index, _ ->
                    predefinedColors[index % predefinedColors.size]
                }
                setColors(colors)
                valueTextColor = android.graphics.Color.BLACK
                valueTextSize = 10f
            }

            // Configura o eixo X para exibir os nomes dos exercícios
            barChart.xAxis.apply {
                // Aqui usamos o campo 'exerciseName' da lista, que deve ser o nome do exercício
                valueFormatter = IndexAxisValueFormatter(exerciseComparison.map { it.exerciseName })
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                labelRotationAngle = -45f
            }

            // Garante que o eixo Y inicie em 0
            barChart.axisLeft.apply {
                axisMinimum = 0f
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