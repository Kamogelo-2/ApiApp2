package com.example.betabudget

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.betabudget.data.Todo
import com.example.betabudget.net.RetrofitInstance
import com.example.betabudget.util.SessionManager
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.betabudget.data.TodoRepository


class AddTodoActivity : AppCompatActivity() {

    private const val TAG = "AddTodoActivity"

    private lateinit var etTodoDescription: EditText
    private lateinit var btnSaveTodo: Button

    private lateinit var repository: TodoRepository
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo) // Use new layout

        sessionManager = SessionManager(this)
        repository = TodoRepository(this)
        userId = sessionManager.fetchUserId()
        if (userId == -1) {
            Log.w(TAG, "No user ID found. Finishing activity.")
            finish()
            return
        }

        etTodoDescription = findViewById(R.id.etTodoDescription)
        btnSaveTodo = findViewById(R.id.btnSaveTodo)

        btnSaveTodo.setOnClickListener {
            saveTodo()
        }
    }

    private fun saveTodo() {
        val description = etTodoDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(TAG, "Saving todo to API...")

        // Create the new Todo object
        val newTodo = Todo(
            todo = description,
            completed = false,
            userId = userId,
            isSynced = false
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.addTodo(newTodo)
                if (response.isSuccessful && response.body() != null) {
                    Log.i(TAG, "Save successful: ${response.body()}")
                    Toast.makeText(baseContext, "Todo saved!", Toast.LENGTH_SHORT).show()

                    // --- 2. CALL THE NOTIFICATION FUNCTION ---
                    showTodoSavedNotification(description)

                    finish() // Go back to MainActivity
                } else {
                    Log.w(TAG, "Save failed: ${response.message()}")
                    Toast.makeText(baseContext, "Save failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Save exception", e)
            }
        }

    }// --- 3. ADD THIS NEW FUNCTION ---
    private fun showTodoSavedNotification(todoDescription: String) {
        if (!sessionManager.areNotificationsEnabled()) {
            Log.d(TAG, "Notification skipped: Disabled by user setting.")
            return
        }
        // We must check if the app has permission *before* building
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted. Don't show the notification.
            Log.w(TAG, "Cannot show notification: Permission not granted.")
            return
        }

        val builder = NotificationCompat.Builder(this, MyApplication.TODO_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_add) // Using an icon we already have
            .setContentTitle("Todo Saved")
            .setContentText(todoDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss notification when user taps it

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification
            val notificationId = System.currentTimeMillis().toInt()
            notify(notificationId, builder.build())
            Log.i(TAG, "Notification sent.")
        }
    }

}