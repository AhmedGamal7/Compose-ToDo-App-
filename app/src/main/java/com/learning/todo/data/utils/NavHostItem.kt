package com.learning.todo.data.utils

sealed class NavHostItem(val name: String, val route: String) {
    object AllTask : NavHostItem("AllTask", "AllTasks")
    object AddEdit : NavHostItem("Add_Edit", "AddEditScreen")
    object DeleteDialog : NavHostItem("DeleteDialog", "DeleteDialog")
}