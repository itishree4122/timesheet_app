package com.example.myapplication


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.interface_api.RegisterRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var fullName: TextInputLayout
    private lateinit var fullNameInput: EditText

    private lateinit var userName: TextInputLayout
    private lateinit var userNameInput: EditText

    private lateinit var password: TextInputLayout
    private lateinit var passwordInput: EditText

    private lateinit var password2: TextInputLayout
    private lateinit var password2Input: EditText

    private lateinit var role: TextInputLayout
    private lateinit var roleInput: AutoCompleteTextView

    private lateinit var registerBtn: Button

    private lateinit var loadingProgressBar: ProgressBar

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fullName = findViewById(R.id.fullName)
        fullNameInput = findViewById(R.id.fullNameEditText)
        userName = findViewById(R.id.username)
        userNameInput = findViewById(R.id.usernameEditText)
        password = findViewById(R.id.password)
        passwordInput = findViewById(R.id.passwordEditText)
        password2 = findViewById(R.id.confirmPassword)
        password2Input = findViewById(R.id.confirmPasswordEditText)
        role = findViewById(R.id.role)
        roleInput = findViewById(R.id.roleDropdown)
        registerBtn = findViewById(R.id.regstrBtn)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val roles = listOf("Supervisor", "Other") // Example roles

// Create an ArrayAdapter with the role list
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)

// Assign the adapter to the roleInput dropdown
        roleInput.setAdapter(adapter)

// Handle item selection
        roleInput.setOnItemClickListener { parent, view, position, id ->
            val selectedRole = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Selected role: $selectedRole", Toast.LENGTH_SHORT).show()
        }


        registerBtn.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            val fullName = fullNameInput.text.toString().trim()
            val userName = userNameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = password2Input.text.toString().trim()
            val role = roleInput.text.toString().trim()

            // Basic validation
            if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the request object
            val registerRequest =
                RegisterRequest(fullName, userName, password, confirmPassword, role)

            // Make the API call
            RetrofitClient.registerApi.registerUser(registerRequest).enqueue(object :
                Callback<Response<Unit>> {
                override fun onResponse(
                    call: Call<Response<Unit>>,
                    response: Response<Response<Unit>>
                ) {
                    loadingProgressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        // Registration successful
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigate to the LoginActivity
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Close the RegisterActivity
                    } else if (response.code() == 400) {
                        // Handle user already registered case
                        Toast.makeText(
                            this@RegisterActivity,
                            "User already registered. Please log in.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Handle other unsuccessful responses
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Response<Unit>>, t: Throwable) {
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(
                        this@RegisterActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

    }
}