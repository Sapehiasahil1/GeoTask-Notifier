package com.example.geo_tasknotifier.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.geo_tasknotifier.MainActivity
import com.example.geo_tasknotifier.R
import com.example.geo_tasknotifier.data.local.TaskDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == false && geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val taskId = intent.getIntExtra("taskId", -1)
            CoroutineScope(Dispatchers.Main).launch {
                val db = TaskDatabase.getDatabase(context)
                val task = withContext(Dispatchers.IO) {
                    db.taskDao().getTaskById(taskId)
                }
                val notification = NotificationCompat.Builder(context, "geo_task_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Task Nearby")
                    .setContentTitle(task.taskContent ?: "Check your task !")
                    .setAutoCancel(true)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, MainActivity::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .build()

                val notificationManager = NotificationManagerCompat.from(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(
                        NotificationChannel(
                            "geo_task_channel",
                            "Geo Tasks",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                    )

                }
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(taskId, notification)
                    db.taskDao().deleteTask(task)
                }
            }
        }

    }
}