package com.learning.todo.screens.tasks

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.learning.todo.R
import com.learning.todo.data.models.Task
import com.learning.todo.data.utils.NavHostItem
import com.learning.todo.screens.TaskViewModel
import com.learning.todo.ui.shared.SearchView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.viewModel


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AllTasks(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val viewModel: TaskViewModel by viewModel()
    val list by viewModel.taskList.collectAsState(initial = emptyList())
    val snackBarHostState = remember { SnackbarHostState() }
    val scaffoldState = rememberScaffoldState(snackbarHostState = snackBarHostState)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            SetUpTopBar(viewModel, navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp), onClick = {
                    // open add_edit composable without any parameter.
                    navController.currentBackStackEntry?.savedStateHandle?.set("title", "Add Task")
                    navController.currentBackStackEntry?.savedStateHandle?.set("task", null)
                    navController.navigate(NavHostItem.AddEdit.route)
                }) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            itemsIndexed(list, key = { _, listItem ->
                listItem.hashCode()
            }) { _, item ->

                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToStart) {
                            scope.launch {
                                viewModel.deleteTask(item)
                                //show snackBar with undo action.
                                scope.launch {
                                    val result = snackBarHostState.showSnackbar(
                                        message = "Undo Delete Task ?",
                                        duration = SnackbarDuration.Long,
                                        actionLabel = "Undo"
                                    )
                                    when (result) {
                                        SnackbarResult.ActionPerformed -> {
                                            viewModel.unDoDeleteAction(item)
                                        }
                                        //dismiss action
                                        else -> {
                                            //
                                        }
                                    }
                                }
                            }
                        }
                        true
                    }
                )

                SwipeToDismiss(
                    modifier = Modifier.animateItemPlacement(),
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { dismissDirection ->
                        FractionalThreshold(if (dismissDirection == DismissDirection.EndToStart) 0.1f else 0.5f)
                    },
                    background = {
                        DeleteContentRow(dismissState = dismissState)
                    },
                    dismissContent = {
                        val itemState = remember { mutableStateOf(item.completed) }
                        TaskListRow(navController = navController, task = item, check = itemState) {
                            scope.launch {
                                itemState.value = it
                                viewModel.onTaskCheckBoxSelect(item, it)
                                Log.i("Ahmed", "${item.name} :: $it")
                            }
                        }
                    }
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun DeleteContentRow(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.White
            else -> Color.Red
        }
    )
    val alignment = Alignment.CenterEnd
    val icon = Icons.Default.Delete
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0f else 1f
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = alignment
    ) {
        Icon(imageVector = icon, contentDescription = "", modifier = Modifier.scale(scale))
    }
}

@ExperimentalMaterialApi
@Composable
fun TaskListRow(
    navController: NavHostController,
    task: Task,
    check: MutableState<Boolean>,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clickable {
                // open add_edit composable with task object .
                navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                navController.currentBackStackEntry?.savedStateHandle?.set("title", "Edit Task")
                navController.navigate(NavHostItem.AddEdit.route)
                Log.i("MyAppTag", "${task.name} Row Clicked")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = check.value,
            onCheckedChange = {
                onChange(it)
            }
        )

        val lineThrough = if (check.value) TextDecoration.LineThrough else TextDecoration.None

        Text(
            modifier = Modifier
                .padding(2.dp)
                .weight(1f),
            text = "${task.name} ",
            textAlign = TextAlign.Start,
            style = TextStyle(textDecoration = lineThrough, fontSize = 20.sp)
        )

        if (task.important) {
            Image(Icons.Default.Check, contentDescription = "")
        }
    }
}


@Composable
fun SetUpTopBar(viewModel: TaskViewModel, navController: NavHostController) {
    var expandSortMenu by rememberSaveable { mutableStateOf(false) }
    var expandMoreMenu by rememberSaveable { mutableStateOf(false) }
    var showSearchView by rememberSaveable { mutableStateOf(false) }
    var hideComplete by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = "ToDo") },
        actions = {

            //searchView
            if (showSearchView) {
                SearchView(state = viewModel.searchQuery) {
                    showSearchView = it
                }
            } else {
                // icon search view
                IconButton(onClick = {
                    showSearchView = true
                }) {
                    Icon(Icons.Filled.Search, contentDescription = "")
                }
            }

            //sort item
            IconButton(onClick = { expandSortMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = expandSortMenu,
                onDismissRequest = { expandSortMenu = false },
                modifier = Modifier.background(Color.LightGray)
            ) {
                DropdownMenuItem(onClick = {
                    viewModel.onSortSelected(true)
                    expandSortMenu = false
                }) {
                    Text("Sort By Name")
                }
                Divider()
                DropdownMenuItem(onClick = {
                    viewModel.onSortSelected(false)
                    expandSortMenu = false
                }) {
                    Text("Sort By Date")
                }
            }

            //more action
            //run once .
            LaunchedEffect(key1 = "hide_complete") {
                hideComplete = viewModel.preferencesFlow.first().hideComplete
            }
            IconButton(onClick = {
                expandMoreMenu = true
            }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = ""
                )
            }
            DropdownMenu(
                expanded = expandMoreMenu,
                onDismissRequest = { expandMoreMenu = false },
                modifier = Modifier.background(Color.LightGray)
            ) {
                DropdownMenuItem(onClick = {
                    expandMoreMenu = false
                }) {
                    Checkbox(
                        checked = hideComplete,
                        onCheckedChange = {
                            hideComplete = it
                            viewModel.onHideCompleteSelected(it)
                        }
                    )
                    Text("Hide Complete")
                }
                Divider()
                DropdownMenuItem(onClick = {
                    //call
                    expandMoreMenu = false
                    navController.navigate(NavHostItem.DeleteDialog.route)
                }) {
                    Text("Delete All Completed Tasks ? ")
                }
            }

        }

    )

}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun TasksListPreview() {
    val navController = rememberNavController()
    AllTasks(navController = navController)
}