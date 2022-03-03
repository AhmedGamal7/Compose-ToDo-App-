package com.learning.todo.screens.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.learning.todo.data.models.Task
import com.learning.todo.screens.TaskViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel

@Composable
fun AddEditScreen(navController: NavHostController, task: Task?, title: String?) {

    var taskName by remember { mutableStateOf("") }
    var importantCheckBox by remember { mutableStateOf(false) }

    if (task != null) {
        taskName = task.name
        importantCheckBox = task.important
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarHostState)
    val coroutineScope = rememberCoroutineScope()
    val viewModel: TaskViewModel by viewModel()
    val focusManger = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(text = title ?: " ")
            })
        },
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    coroutineScope.launch {
                        if (taskName.isNotBlank()) {
                            if (task != null) {
                                //edit task
                                viewModel.updateTask(task, taskName, importantCheckBox)
                                focusManger.clearFocus()
                                snackBarHostState.showSnackbar(
                                    message = "Updated",
                                    duration = SnackbarDuration.Short,
                                )
                            } else {
                                //add new task
                                viewModel.insertTask(
                                    Task(
                                        name = taskName,
                                        important = importantCheckBox
                                    )
                                )
                                focusManger.clearFocus()
                                snackBarHostState.showSnackbar(
                                    message = "Added",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }
                    }//coroutinesScope
                    navController.popBackStack()
                }) {
                Icon(Icons.Default.Done, contentDescription = "")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = taskName,
                onValueChange = {
                    taskName = it
                },
                label = { Text(text = "Task Name") },

                )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //importantCheckBox = !importantCheckBox
                    },
                verticalAlignment = Alignment.CenterVertically

            ) {
                Checkbox(
                    checked = importantCheckBox,
                    onCheckedChange = {
                        importantCheckBox = it
                    }
                )
                Text(
                    modifier = Modifier
                        .padding(2.dp),
                    text = "Important Task ?",
                    textAlign = TextAlign.Start
                )
            }
            if (task != null) {
                Text(
                    text = "Create ${task.createDateFormatted} ",
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}




