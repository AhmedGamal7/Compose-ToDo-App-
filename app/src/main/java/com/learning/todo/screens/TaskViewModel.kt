package com.learning.todo.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.todo.data.models.Task
import com.learning.todo.data.preferences.SettingPreferences
import com.learning.todo.data.room.TaskDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskDao: TaskDao,
    private val settingPreferences: SettingPreferences
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferencesFlow = settingPreferences.settingFlow

    @ExperimentalCoroutinesApi
    private val taskFlow = combine(searchQuery, preferencesFlow) { searchQuery, filterPreferences ->
        Pair(searchQuery, filterPreferences)
    }.flatMapLatest {
        taskDao.getAllTask(it.first, it.second.sortType, it.second.hideComplete)
    }

    val taskList = taskFlow

    fun onSortSelected(sortType: Boolean) = viewModelScope.launch {
        settingPreferences.updateSortType(sortType = sortType)
    }

    fun onHideCompleteSelected(hideComplete: Boolean) = viewModelScope.launch {
        settingPreferences.updateHideComplete(hideComplete = hideComplete)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    suspend fun onTaskCheckBoxSelect(task: Task, isComplete: Boolean) {
        taskDao.update(task = task.copy(completed = isComplete))

    }

    fun unDoDeleteAction(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task, taskName: String, important: Boolean) =
        viewModelScope.launch {
            taskDao.update(task = task.copy(name = taskName, important = important))
        }
}