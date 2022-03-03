package com.learning.todo.di

import android.app.Application
import androidx.room.Room
import com.learning.todo.data.preferences.SettingPreferences
import com.learning.todo.data.room.TaskDao
import com.learning.todo.data.room.TaskDataBase
import com.learning.todo.screens.TaskViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val TaskDB = module {
    fun provideDataBase(application: Application): TaskDataBase {
        return Room.databaseBuilder(application, TaskDataBase::class.java, "TaskDataBase")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideDao(dataBase: TaskDataBase): TaskDao {
        return dataBase.taskDao()
    }

    single { provideDataBase(androidApplication()) }
    single { provideDao(get()) }
    single { SettingPreferences() }
}

val taskRepository = module {

}
val taskViewModel = module {
    viewModel {
        TaskViewModel(get(),get())
    }
}