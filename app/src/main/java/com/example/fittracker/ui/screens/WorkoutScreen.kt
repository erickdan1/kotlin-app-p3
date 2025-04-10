package com.example.fittracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
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
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Registrar Atividade",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Botão para adicionar nova atividade
            AddWorkoutButton(onClick = { showDialog = true })

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
fun AddWorkoutButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nova Atividade",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout) {
    val backgroundColor = getWorkoutColor(workout.exerciseType).copy(alpha = 0.15f)
    val borderColor = getWorkoutColor(workout.exerciseType).copy(alpha = 0.4f)
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = workout.exerciseName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                text = "Tipo: ${workout.exerciseType}",
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.85f)
            )
            Text(
                text = "Duração: ${workout.duration} min",
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.75f)
            )
            Text(
                text = "Data: ${formatDate(workout.date)}",
                fontSize = 14.sp,
                color = textColor.copy(alpha = 0.6f)
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
        "Academia" -> Color(0xFFEF6C00)   // Laranja queimado
        else -> Color(0xFF9E9E9E)         // Cinza
    }
}