package com.learning.todo.data.room

import androidx.room.*
import com.learning.todo.data.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getAllTask(query: String, sortType: Boolean, hideCompleted: Boolean): Flow<List<Task>> =
        if (sortType) {
            allTaskSortedByName(query = query, hideCompleted = hideCompleted)
        } else {
            allTaskSortedByDate(query = query, hideCompleted = hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%'||:query||'%' ORDER BY important DESC ,name")
    fun allTaskSortedByName(query: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%'||:query||'%' ORDER BY important DESC ,created")
    fun allTaskSortedByDate(query: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task): Int

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE completed = 1 ")
    suspend fun deleteCompletedTasks()

}