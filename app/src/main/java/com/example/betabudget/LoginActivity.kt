package com.example.betabudget
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.betabudget.data.LoginRequest
import com.example.betabudget.net.RetrofitInstance
import com.example.betabudget.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private const val TAG = "LoginActivity"
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: LoginActivity started.")

        sessionManager = SessionManager(this)

        // Find views
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // Update hints for API
        etUsername.hint = "Username (e.g., kminchelle)"
        etUsername.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        etPassword.hint = "Password (e.g., 0lelplR)"

        btnLogin.setOnClickListener {
            Log.i(TAG, "Login button clicked.")
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            handleLogin(username, password)
        }

        tvGoToRegister.setOnClickListener {
            Log.i(TAG, "Go to Register clicked.")
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is already signed in
        if (sessionManager.fetchAuthToken() != null) {
            Log.i(TAG, "User already logged in. Navigating to Main.")
            navigateToMainActivity()
        }
    }

    private fun handleLogin(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Username and password required", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(TAG, "Attempting API login for user: $username")

        // Use Coroutines to make the network call on a background thread
        lifecycleScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = RetrofitInstance.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.i(TAG, "Login successful for user: ${loginResponse.username}")

                    // Save token and user ID
                    sessionManager.saveAuthToken(loginResponse.token)
                    sessionManager.saveUserId(loginResponse.id)

                    navigateToMainActivity()
                } else {
                    Log.w(TAG, "Login failed: ${response.message()}")
                    Toast.makeText(baseContext, "Login failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login exception", e)
                Toast.makeText(baseContext, "Login error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}