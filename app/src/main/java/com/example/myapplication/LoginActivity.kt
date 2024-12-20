package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.LoginRequest
import com.example.myapplication.interface_api.LoginResponse
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {


    private lateinit var rootView: View
    private lateinit var scrollView: ScrollView // Reference to your ScrollView
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var username: EditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var password: EditText
    private lateinit var fpassword: TextView
    private lateinit var loginbutton: Button
    private lateinit var registerText: TextView
    private lateinit var loadingProgressBar: ProgressBar



    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.button)
        // Initialize ViewModel with factory

        usernameLayout = findViewById(R.id.usernameLayout)
        username = findViewById(R.id.username)
        passwordLayout = findViewById(R.id.passwordLayout)
        password = findViewById(R.id.password)
        fpassword = findViewById(R.id.forgotPassword)
        loginbutton = findViewById(R.id.loginButton)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        loginbutton.setOnClickListener {

            // Validate the form
            if (validateForm()) {
                // Show the loading indicator
                loadingProgressBar.visibility = View.VISIBLE

                // Call the login function with the user's credentials
                loginUser(username.text.toString(), password.text.toString())
            } else {
                // If validation fails, ensure the loading indicator is hidden
                loadingProgressBar.visibility = View.GONE
            }
        }

        fpassword.setOnClickListener {
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
        }

        // Get the root view and scroll view
        scrollView = findViewById(R.id.scrollView) // Ensure to get the correct ScrollView
        rootView = findViewById(R.id.main)

    }

    private fun validateForm(): Boolean {
        val username1 = username.text.toString().trim()
        val password1 = password.text.toString().trim()


        if (username1.isEmpty()) {
            usernameLayout.error = "Username is required"
            username.requestFocus()
            return false
        } else {
            usernameLayout.error = null
        }

        if (password1.isEmpty()) {
            passwordLayout.error = "Password is required"
            password.requestFocus()
            return false
        } else if (password1.length < 6 || password1.length > 12) {
            passwordLayout.error = "Password must be 6 to 12 characters"
            password.requestFocus()
            return false
        } else {
            passwordLayout.error = null
        }

        return true
    }

    private fun loginUser(username: String, password: String) {
        loadingProgressBar.visibility = View.VISIBLE

        val loginRequest = LoginRequest(username, password)

        RetrofitClient.apiService.loginUser(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    loadingProgressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            val accessToken = it.Token.access
                            val refreshToken = it.Token.refresh
                            val isAdmin = it.user.is_admin
                            val supervisorName = it.user.user_name
                            val username = it.user.name

                            // Save tokens and admin status
                            SharedPreferencesHelper.saveAccessToken(this@LoginActivity, accessToken)
                            SharedPreferencesHelper.saveRefreshToken(this@LoginActivity, refreshToken)
                            SharedPreferencesHelper.saveIsAdmin(this@LoginActivity, isAdmin)
                            SharedPreferencesHelper.saveSupervisorName(this@LoginActivity, supervisorName)
                            SharedPreferencesHelper.saveUsername(this@LoginActivity, username)
                            Log.d("Login", "is_admin: ${it.user.is_admin}")

                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // Navigate to HomeActivity
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



}





