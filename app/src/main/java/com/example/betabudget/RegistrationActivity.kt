package com.example.betabudget
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.betabudget.net.RetrofitInstance
import kotlinx.coroutines.launch

class RegistrationActivity: AppCompatActivity() {
    private  val TAG = "RegistrationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        Log.d(TAG, "onCreate: RegistrationActivity started.")

        // The API expects more fields, but we'll send the basics
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        etUsername.hint = "Username"
        etUsername.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        btnRegister.setOnClickListener {
            Log.i(TAG, "Register button clicked.")
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            handleRegistration(username, password, confirmPassword)
        }
    }

    private fun handleRegistration(username: String, pass: String, confirmPass: String) {
        if (username.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confirmPass) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(TAG, "Attempting API registration for user: $username")

        // Create a map of data to send
        val userData = mapOf(
            "username" to username,
            "password" to pass
            // We could add more fields here like email, firstName, etc.
        )

        lifecycleScope.launch {
            try {
                // The API will simulate a successful creation
                val response = RetrofitInstance.api.register(userData)
                if (response.isSuccessful) {
                    Log.i(TAG, "Registration simulation successful.")
                    Toast.makeText(baseContext, "Registration successful! Please log in.", Toast.LENGTH_LONG).show()
                    finish() // Go back to LoginActivity
                } else {
                    Log.w(TAG, "Registration failed: ${response.message()}")
                    Toast.makeText(baseContext, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Registration exception", e)
                Toast.makeText(baseContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}