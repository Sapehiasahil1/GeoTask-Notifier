package com.example.geo_tasknotifier

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.example.geo_tasknotifier.ui.theme.Geo_TaskNotifierTheme
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

    private val PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Geo_TaskNotifierTheme {
                val navController = rememberNavController()
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
        }
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.zip(grantResults.toList()).filter { it.second != PackageManager.PERMISSION_GRANTED }
            if (deniedPermissions.isEmpty()) {
                Log.d("Permissions", "All permissions granted")
            } else {
                Toast.makeText(this, "Some permissions are required for full functionality", Toast.LENGTH_LONG).show()
            }
        }
    }
}

