package com.example.betabudget.data

import android.content.Context
import android.util.Log
import com.example.betabudget.net.RetrofitInstance
import com.example.betabudget.util.NetworkUtils

class TodoRepository (context: Context) {
    private val TAG = "TodoRepository"

    // Get instances of API and Room DAO
    private val api = RetrofitInstance.api
    private val dao = AppDatabase.getDatabase(context).todoDao()
    private val appContext = context.applicationContext

    /**
     * Get all todos for the main screen.
     * 1. Fetches from the API.
     * 2. Fetches unsynced local todos.
     * 3. Combines and returns them.
     */
    suspend fun getAllTodos(userId: Int): List<Todo> {
        // Try to fetch todos from the remote API
        val remoteTodos = try {
            val response = api.getUserTodos(userId)
            if (response.isSuccessful) {
                response.body()?.todos ?: emptyList()
            } else {
                Log.w(TAG, "Failed to fetch remote todos: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching remote todos", e)
            emptyList()
        }

        // Get any locally created todos that haven't been synced
        val localUnsyncedTodos = dao.getUnsyncedTodos(userId)

        // Return unsynced todos first, then the rest
        return localUnsyncedTodos + remoteTodos
    }

    /**
     * Saves a new todo to the local Room database.
     * This is called by AddTodoActivity and is very fast.
     */
    suspend fun addTodoOffline(todo: Todo) {
        dao.insertTodo(todo)
        Log.i(TAG, "Saved todo to local RoomDB: ${todo.todo}")
    }

    /**
     * This is the core sync logic.
     * It finds all unsynced todos and sends them to the API.
     */
    suspend fun syncOfflineTodos(userId: Int) {
        if (!NetworkUtils.isNetworkAvailable(appContext)) {
            Log.w(TAG, "Sync failed: No internet connection.")
            return
        }

        val unsyncedTodos = dao.getUnsyncedTodos(userId)
        if (unsyncedTodos.isEmpty()) {
            Log.d(TAG, "No todos to sync.")
            return
        }

        Log.i(TAG, "Syncing ${unsyncedTodos.size} todos...")

        for (todo in unsyncedTodos) {
            try {
                // Send the local todo (minus localId) to the API
                val response = api.addTodo(todo)

                if (response.isSuccessful && response.body() != null) {
                    // Sync successful! Update the local item.
                    val newApiId = response.body()!!.id!!
                    dao.markTodoAsSynced(todo.localId, newApiId)
                    Log.i(TAG, "Synced todo: ${todo.todo}")
                } else {
                    Log.w(TAG, "Failed to sync todo '${todo.todo}': ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while syncing todo '${todo.todo}'", e)
            }
        }
    }

    /**
     * Clears local database on logout.
     */
    suspend fun clearLocalData() {
        dao.clearAllTodos()
    }
}