package com.example.myapplication

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.backend_api.RetrofitClient.changePasswordService
import com.example.myapplication.backend_api.SharedPreferencesHelper
import com.example.myapplication.interface_api.ChangePassword
import com.example.myapplication.interface_api.ChangePasswordService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePassword : AppCompatActivity() {

    private lateinit var currentPassword: TextInputLayout
    private lateinit var newPassword: TextInputLayout
    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var changePasswordBtn: Button
    private lateinit var loadingProgress: ProgressBar

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        currentPassword = findViewById(R.id.passwordLayout)
        newPassword = findViewById(R.id.passwordLayout2)
        currentPasswordInput = findViewById(R.id.password)
        newPasswordInput = findViewById(R.id.password2)
        changePasswordBtn = findViewById(R.id.changePassword)
        loadingProgress = findViewById(R.id.loadingProgressBar)

        // Set onClickListener for the change password button
        changePasswordBtn.setOnClickListener {
            val currentPasswordText = currentPasswordInput.text.toString().trim()
            val newPasswordText = newPasswordInput.text.toString().trim()

            if (validatePasswords(currentPasswordText, newPasswordText)) {
                // Make the API call to change the password
                changePassword(currentPasswordText, newPasswordText)
            }
        }
    }


private fun validatePasswords(currentPassword: String, newPassword: String): Boolean {
    if (currentPassword.isEmpty() || newPassword.isEmpty()) {
        Toast.makeText(this, "Both fields must be filled out", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

    private fun changePassword(currentPassword: String, newPassword: String) {
        // Show the loading progress bar
        loadingProgress.visibility = View.VISIBLE

        val accessToken = SharedPreferencesHelper.getAccessToken(this)  // Retrieve access token
        val tokenWithBearer = "Bearer $accessToken"  // Ensure token is prefixed with "Bearer "

        val changePasswordService = changePasswordService()  // Create service instance
        val changePasswordRequest = ChangePassword(password = currentPassword, password2 = newPassword)

        // Perform the API call with the access token
        changePasswordService.changePassword(tokenWithBearer, changePasswordRequest).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Hide the progress bar
                loadingProgress.visibility = View.GONE

                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePassword, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    // Clear the input fields
                    currentPasswordInput.text?.clear()
                    newPasswordInput.text?.clear()
                } else {
                    // Handle error response
                    Toast.makeText(this@ChangePassword, "Failed to change password: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Hide the progress bar
                loadingProgress.visibility = View.GONE
                Toast.makeText(this@ChangePassword, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}