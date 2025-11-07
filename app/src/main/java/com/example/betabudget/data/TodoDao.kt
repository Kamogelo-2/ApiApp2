package com.example.betabudget.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    // Insert a new todo. Used in offline mode.
    @Insert
    suspend fun insertTodo(todo: Todo)

    // Get all todos that haven't been synced to the API
    @Query("SELECT * FROM todos WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedTodos(userId: Int): List<Todo>

    // Get all locally-saved todos (for display)
    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY localId DESC")
    suspend fun getAllLocalTodos(userId: Int): List<Todo>

    // Update a local todo after it's synced
    // We set its sync flag and store its new API ID
    @Query("UPDATE todos SET isSynced = 1, id = :apiId WHERE localId = :localId")
    suspend fun markTodoAsSynced(localId: Long, apiId: Int)

    // Clear all local todos (e.g., on logout)
    @Query("DELETE FROM todos")
    suspend fun clearAllTodos()
}
