package com.learning.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.learning.todo.data.models.Task
import com.learning.todo.data.utils.NavHostItem
import com.learning.todo.screens.add.AddEditScreen
import com.learning.todo.screens.dialogs.DeleteDialog
import com.learning.todo.screens.tasks.AllTasks
import com.learning.todo.ui.theme.ToDoTheme

@ExperimentalFoundationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ToDoTheme {
                Scaffold(
                    backgroundColor = colorResource(id = R.color.main_color)
                ) {
                    val navController = rememberNavController()
                    SetUpNavigation(navController)
                }
            }
        }
    }
}


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun SetUpNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavHostItem.AllTask.route) {
        composable(NavHostItem.AllTask.route) {
            AllTasks(navController = navController)
        }
        dialog(NavHostItem.DeleteDialog.route) {
            DeleteDialog(navController = navController)
        }
        composable(NavHostItem.AddEdit.route) {
            val task = navController.previousBackStackEntry?.savedStateHandle?.get<Task?>("task")
            val title = navController.previousBackStackEntry?.savedStateHandle?.get<String>("title")
            AddEditScreen(navController = navController, task = task, title)
        }
    }
}
