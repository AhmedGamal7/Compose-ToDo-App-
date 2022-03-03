package com.learning.todo.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learning.todo.data.models.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDataBase() : RoomDatabase() {
    abstract fun taskDao(): TaskDao

}