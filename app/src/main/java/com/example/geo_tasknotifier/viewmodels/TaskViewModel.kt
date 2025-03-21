package com.example.geo_tasknotifier.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.geo_tasknotifier.model.GeoResponseItem
import com.example.geo_tasknotifier.model.Task
import com.example.geo_tasknotifier.repositories.TaskRepository
import com.example.geo_tasknotifier.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository, context: Context) : ViewModel() {

    val allTasks = taskRepository.allTasks
    private val locationHelper = LocationHelper(context)

    private val _addressSuggestions = MutableStateFlow<List<GeoResponseItem>>(emptyList())
    val addressSuggestions: MutableStateFlow<List<GeoResponseItem>> = _addressSuggestions

    fun insertTaskForCurrLoc(taskTitle: String, taskContent: String) {
        viewModelScope.launch {
            val coordinates = locationHelper.getCurrentLocation()
            val latitude = coordinates?.first
            val longitude = coordinates?.second
            if (latitude != null && longitude != null) {
                taskRepository.insertTask(taskTitle, taskContent, latitude, longitude)
            }
        }
    }

    fun insertTaskForDiffLoc(taskTitle: String, taskContent: String, address: String) {

        viewModelScope.launch {
            val coordinates = getCoordinatesFromAddress(address)
            val latitude = coordinates?.first
            val longitude = coordinates?.second
            if (latitude != null && longitude != null) {
                taskRepository.insertTask(taskTitle, taskContent, latitude, longitude)
            }
        }
    }

    suspend fun getTaskById(taskId: Int): Task = taskRepository.getTaskById(taskId)

    fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }


    suspend fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return taskRepository.getCoordinatesFromAddress(address)
    }

    fun getAddressSuggestions(address: String) {
        viewModelScope.launch {
            if (address.length >= 3) {
                val suggestions = taskRepository.getAddressSuggestions(address)
                if (suggestions != null) {
                    _addressSuggestions.value = suggestions
                } else _addressSuggestions.value = emptyList()
            } else {
                _addressSuggestions.value = emptyList()
            }
        }
    }

    class TaskViewModelFactory(
        private val repository: TaskRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TaskViewModel(repository, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


