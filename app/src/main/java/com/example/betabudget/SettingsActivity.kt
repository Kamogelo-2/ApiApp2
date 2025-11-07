package com.example.betabudget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.betabudget.data.TodoRepository
import com.example.betabudget.util.SessionManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"

    private lateinit var tvLogout: TextView
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: TodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Use new layout

        sessionManager = SessionManager(this)

        tvLogout = findViewById(R.id.tvLogout)
        tvLogout.setOnClickListener {
            Log.i(TAG, "Logout button clicked.")

            // Clear the session
            sessionManager.clearData()

            // NEW: Clear the local database
            lifecycleScope.launch {
                repository.clearLocalData()
            }

            // Go to login
            goToLogin()
        }
    }

    private fun goToLogin() {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
