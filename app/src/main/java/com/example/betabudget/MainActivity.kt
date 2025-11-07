package com.example.betabudget

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betabudget.data.Todo
import com.example.betabudget.net.RetrofitInstance
import com.example.betabudget.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


import androidx.fragment.app.Fragment
import com.example.betabudget.fragments.SettingsFragment
import com.example.betabudget.fragments.TodoListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    // UI
    private lateinit var rvTodos: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var fabAddTodo: FloatingActionButton
    private lateinit var tvEmptyView: TextView
    private lateinit var ibSettings: ImageButton


    // Data
    private var todoList = mutableListOf<Todo>()

    // Session
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "Notification permission granted.")
        } else {
            Log.w(TAG, "Notification permission denied.")
            Toast.makeText(this, "Notifications will be disabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Use new layout

        sessionManager = SessionManager(this)
        userId = sessionManager.fetchUserId()

        if (userId == -1) {
            Log.w(TAG, "No user logged in. Returning to LoginActivity.")
            goToLogin()
            return


        }
        Log.d(TAG, "onCreate: User is logged in: $userId")

        // Find views
        rvTodos = findViewById(R.id.rvTodos)
        fabAddTodo = findViewById(R.id.fabAddTodo)
        tvEmptyView = findViewById(R.id.tvEmptyView)
        ibSettings = findViewById(R.id.ibSettings)

        setupRecyclerView()
        setupClickListeners()

        checkAndRequestNotificationPermission()
    }
    private fun checkAndRequestNotificationPermission() {
        // Only needed for Android 13 (API 33) and up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is already granted
                    Log.d(TAG, "Notification permission already granted.")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Explain to the user why you need the permission
                    // (You would show a dialog here)
                    Log.w(TAG, "Showing notification permission rationale.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly ask for the permission
                    Log.i(TAG, "Requesting notification permission.")
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list every time we return to this screen
        loadTodos()
    }

    private fun setupClickListeners() {
        fabAddTodo.setOnClickListener {
            Log.i(TAG, "FAB (Add Todo) clicked.")
            startActivity(Intent(this, AddTodoActivity::class.java))
        }

        ibSettings.setOnClickListener {
            Log.i(TAG, "Settings button clicked.")
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Initializing.")
        todoAdapter = TodoAdapter(todoList)
        rvTodos.adapter = todoAdapter
        rvTodos.layoutManager = LinearLayoutManager(this)
    }

    private fun loadTodos() {
        Log.i(TAG, "loadTodos: Loading from API for user $userId")

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getUserTodos(userId)
                if (response.isSuccessful && response.body() != null) {
                    val todos = response.body()!!.todos
                    Log.d(TAG, "loadTodos: Success. Found ${todos.size} todos.")
                    todoList.clear()
                    todoList.addAll(todos)
                    todoAdapter.notifyDataSetChanged()
                    checkEmptyView()
                } else {
                    Log.w(TAG, "loadTodos: Error: ${response.message()}")
                    Toast.makeText(baseContext, "Error loading todos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadTodos: Exception", e)
            }
        }
    }

    private fun checkEmptyView() {
        if (todoList.isEmpty()) {
            rvTodos.visibility = View.GONE
            tvEmptyView.visibility = View.VISIBLE
        } else {
            rvTodos.visibility = View.VISIBLE
            tvEmptyView.visibility = View.GONE
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}