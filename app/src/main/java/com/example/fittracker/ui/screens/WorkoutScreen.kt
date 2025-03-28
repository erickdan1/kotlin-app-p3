package com.example.fittracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fittracker.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.fittracker.data.model.Workout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel, navController: NavController) {
    val workouts by viewModel.workouts.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Atividade") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Botão para adicionar nova atividade
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nova Atividade")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(workouts) { workout ->
                    WorkoutItem(workout)
                }
                item {
                    LaunchedEffect(Unit) { viewModel.loadWorkouts() }
                }
            }
        }
    }

    // Exibir o diálogo quando showDialog for verdadeiro
    if (showDialog) {
        AddWorkoutDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, type, duration ->
                viewModel.addWorkout(name, type, duration)
            }
        )
    }
}

@Composable
fun WorkoutItem(workout: Workout) {
    val backgroundColor = getWorkoutColor(workout.exerciseType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = workout.exerciseName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Tipo: ${workout.exerciseType}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            Text(
                text = "Duração: ${workout.duration} min",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Text(
                text = "Data: ${formatDate(workout.date)}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var exerciseType by remember { mutableStateOf("Ao ar livre") }
    var selectedExercise by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    // Listas de exercícios baseadas no tipo selecionado
    val outdoorExercises = listOf("Corrida", "Caminhada", "Ciclismo", "Hiking")
    val gymExercises = listOf("Supino", "Agachamento", "Levantamento Terra", "Rosca Direta")

    val exerciseList = if (exerciseType == "Ao ar livre") outdoorExercises else gymExercises

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Adicionar Novo Treino") },
        text = {
            Column {
                // Dropdown para selecionar o tipo do exercício
                val exerciseTypes = listOf("Ao ar livre", "Academia")
                var expandedType by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = exerciseType,
                        onValueChange = {},
                        label = { Text("Tipo de Exercício") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                                modifier = Modifier.clickable { expandedType = true })
                        }
                    )
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        exerciseTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    exerciseType = type
                                    selectedExercise = "" // Resetando o nome do exercício
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown para selecionar o nome do exercício
                var expandedExercise by remember { mutableStateOf(false) }

                Box {
                    OutlinedTextField(
                        value = selectedExercise,
                        onValueChange = {},
                        label = { Text("Nome do Exercício") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null,
                                modifier = Modifier.clickable { expandedExercise = true })
                        }
                    )
                    DropdownMenu(expanded = expandedExercise, onDismissRequest = { expandedExercise = false }) {
                        exerciseList.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise) },
                                onClick = {
                                    selectedExercise = exercise
                                    expandedExercise = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter { char -> char.isDigit() } },
                    label = { Text("Duração (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val durationInt = duration.toIntOrNull() ?: 0
                if (selectedExercise.isNotBlank() && durationInt > 0) {
                    onConfirm(selectedExercise, exerciseType, durationInt)
                    onDismiss()
                }
            }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getMonthYear(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getWorkoutColor(exerciseType: String): Color {
    return when (exerciseType) {
        "Ao ar livre" -> Color(0xFF4CAF50) // Verde
        "Academia" -> Color(0xFFFF5722)   // Laranja
        else -> Color.Gray
    }
}