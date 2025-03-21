package com.example.geo_tasknotifier

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geo_tasknotifier.data.local.TaskDatabase
import com.example.geo_tasknotifier.presentation.CurrLocScreen
import com.example.geo_tasknotifier.presentation.DiffLocScreen
import com.example.geo_tasknotifier.presentation.HomeScreen
import com.example.geo_tasknotifier.presentation.OptionScreen
import com.example.geo_tasknotifier.repositories.TaskRepository
import com.example.geo_tasknotifier.viewmodels.TaskViewModel
import com.example.geo_tasknotifier.viewmodels.TaskViewModel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            TaskRepository(
                TaskDatabase.getDatabase(applicationContext).taskDao(),
                applicationContext
            ),
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            HomeScreen(taskViewModel = taskViewModel,navController)

            NavHost(navController, startDestination = "HomeScreen") {
                composable("HomeScreen"){
                    HomeScreen(taskViewModel = taskViewModel,navController)
                }
                composable("OptionScreen") {
                    OptionScreen(navController)
                }
                composable("CurrLocScreen") {
                    CurrLocScreen(applicationContext, taskViewModel, navController)
                }
                composable("DiffLocScreen") {
                    DiffLocScreen(taskViewModel, applicationContext, navController)
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
        }
    }
}

