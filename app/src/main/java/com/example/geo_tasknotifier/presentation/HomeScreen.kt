package com.example.geo_tasknotifier.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.geo_tasknotifier.model.Task
import com.example.geo_tasknotifier.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    taskViewModel: TaskViewModel,
    navController: NavController
) {

    val tasks by taskViewModel.allTasks.collectAsState(emptyList())
    var taskToDelete by remember { mutableStateOf<Task?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Geo-Task Notifier") },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("OptionScreen")
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { padding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No tasks yet! Tap + to add.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
            ) {
                items(tasks) { task ->
                    TaskCard(task, onItemClick = {
                        taskToDelete = task
                    })
                }
            }
        }

        taskToDelete?.let { task ->
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = {
                    Text("Delete Task")
                },
                text = {
                    Text("Are you sure you want to delete this task?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        taskViewModel.deleteTask(task)
                        taskToDelete = null
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        taskToDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}


@Composable
fun TaskCard(task: Task, onItemClick: (Task) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(task)
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.taskTitle, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = task.taskContent,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
                Text(
                    text = "Lat: ${task.latitude}, Lon: ${task.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}