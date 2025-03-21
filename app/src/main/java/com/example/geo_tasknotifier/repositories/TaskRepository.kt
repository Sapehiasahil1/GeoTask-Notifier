package com.example.geo_tasknotifier.repositories

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.geo_tasknotifier.BuildConfig
import com.example.geo_tasknotifier.util.GeofenceBroadcastReceiver
import com.example.geo_tasknotifier.data.local.TaskDao
import com.example.geo_tasknotifier.data.remote.GeoApi
import com.example.geo_tasknotifier.model.GeoResponseItem
import com.example.geo_tasknotifier.model.Task
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class TaskRepository(private val taskDao: TaskDao, private val context: Context) {

    val allTasks = taskDao.getAllTasks()
    private val geoInstance = GeoApi.getGeoInstance()

    suspend fun insertTask(
        taskTitle: String,
        taskContent: String,
        latitude: Double,
        longitude: Double
    ) {
        val task = Task(
            taskTitle = taskTitle,
            taskContent = taskContent,
            latitude = latitude,
            longitude = longitude
        )
        taskDao.insertTask(task)
        setUpGeofence(task)
    }

    suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double> {
        return try {
            val response =
                geoInstance.getCoordinates(address, BuildConfig.API_KEY)
            val lat = response[0].lat.toDouble()
            val lon = response[0].lon.toDouble()
            lat to lon
        } catch (e: Exception) {
            Log.e("GeoTaskNotifier", "Error getting coordinates: ${e.message}")
            0.0 to 0.0
        }
    }

    suspend fun getAddressSuggestions(address: String): List<GeoResponseItem>? {
        return try {
            geoInstance.getCoordinates(address, BuildConfig.API_KEY)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error getting address suggestions: ${e.message}")
            null
        }
    }

    suspend fun getTaskById(taskId: Int): Task = taskDao.getTaskById(taskId)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    private fun setUpGeofence(task: Task) {
        val geofencingClient = LocationServices.getGeofencingClient(context)
        val geofence = Geofence.Builder()
            .setRequestId(task.taskId.toString())
            .setCircularRegion(task.latitude, task.longitude, 50f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val request = GeofencingRequest.Builder()
            .addGeofence(geofence)
//            .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            .putExtra("taskId", task.taskId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(request, pendingIntent)
                .addOnSuccessListener { Log.d("Geofence", "Added for task ${task.taskId}") }
                .addOnFailureListener { Log.e("Geofence", "Failed: ${it.message}") }
        }
    }
}