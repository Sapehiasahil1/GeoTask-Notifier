package com.example.geo_tasknotifier.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.geo_tasknotifier.model.GeoResponseItem
import com.example.geo_tasknotifier.viewmodels.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffLocScreen(
    taskViewModel: TaskViewModel,
    context: Context,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<GeoResponseItem?>(null) }
    val suggestions by taskViewModel.addressSuggestions.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Different Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Task Content") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box {
                OutlinedTextField(
                    value = location!!,
                    onValueChange = {
                        location = it
                    },
                    label = { Text("Search Location") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.Place, contentDescription = "Location") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            location?.let { taskViewModel.getAddressSuggestions(it) }
                            expanded = true
                        }
                    )
                )
                DropdownMenu(
                    expanded = expanded && suggestions.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .align(Alignment.BottomStart)
                        .background(MaterialTheme.colorScheme.surface)
                        .height(200.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    suggestion.display_name ?: "Unknown",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                selectedLocation = suggestion
                                location = suggestion.display_name ?: ""
                                expanded = false
                            }
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    selectedLocation?.let { loc ->
                        taskViewModel.insertTaskForDiffLoc(
                            title,
                            content,
                            loc.display_name
                        )
                        title = ""
                        content = ""
                        location = ""
                        selectedLocation = null
                        Toast.makeText(context, "Task Created !!", Toast.LENGTH_SHORT).show()
                        navController.navigate("HomeScreen")
                    } ?: Toast.makeText(
                        context,
                        "Please selecte a location ",
                        Toast.LENGTH_SHORT
                    ).show()

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = content.isNotBlank() && selectedLocation != null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Task")
            }
        }

    }
}