package com.learning.todo.screens.dialogs

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.learning.todo.screens.TaskViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel

@Composable
fun DeleteDialog(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: TaskViewModel by viewModel()
    AlertDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        title = {
            Text(text = "Delete Completed")
        },
        text = {
            Text("Are you sure to delete all completed tasks ?")
        },
        confirmButton = {
            Button(
                onClick = {
                    // call delete fun in view model
                    coroutineScope.launch {
                        viewModel.deleteCompletedTasks()
                    }
                    navController.popBackStack()
                }) {
                Text("Ok")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    navController.popBackStack()
                }) {
                Text("Cancel")
            }
        }
    )
}