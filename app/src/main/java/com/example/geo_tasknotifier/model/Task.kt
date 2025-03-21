package com.example.geo_tasknotifier.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val taskTitle: String,
    val taskContent: String,
    val latitude: Double,
    val longitude: Double,
    val timeStamp: Long = System.currentTimeMillis()
)
