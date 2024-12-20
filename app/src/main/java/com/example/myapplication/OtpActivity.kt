package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.backend_api.RetrofitClient
import com.example.myapplication.interface_api.OtpRequest
import com.example.myapplication.interface_api.ResetPassword
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText

    private lateinit var sendOtp: Button

    private lateinit var otp: TextInputLayout
    private lateinit var otpInput: EditText
    private lateinit var password: TextInputLayout
    private lateinit var passwordInput: EditText
    private lateinit var resetPassword: Button
    private lateinit var otpProgressBar: ProgressBar
    private lateinit var resetPasswordProgressBar: ProgressBar

    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_otp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.emailEditText)
        otp = findViewById(R.id.enterOtp)
        otpInput = findViewById(R.id.enterOtpEditText)
        password = findViewById(R.id.enterPassword)
        passwordInput = findViewById(R.id.enterPasswordEditText)
        resetPassword = findViewById(R.id.resetPassword)
        otpProgressBar = findViewById(R.id.sendOtprogressBar)
        resetPasswordProgressBar = findViewById(R.id.resetPasswordprogressBar)

        text1 = findViewById(R.id.emailtext)
        text2 = findViewById(R.id.otptext)
        text3 = findViewById(R.id.passwordtext)

        // Initially hide OTP and password fields along with reset password button
        text2.visibility = View.GONE
        text3.visibility = View.GONE
        otp.visibility = View.GONE
        password.visibility = View.GONE
        resetPassword.visibility = View.GONE

        sendOtp = findViewById(R.id.otp)
        sendOtp.setOnClickListener {
            sendOtp()
        }

        // Set click listener for reset password button
        resetPassword.setOnClickListener {

            val otpText = otpInput.text.toString().trim()
            val newPassword = passwordInput.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            if (otpText.isNotEmpty() && newPassword.isNotEmpty() && email.isNotEmpty()) {
                resetPassword(email, otpText, newPassword)
            } else {
                resetPasswordProgressBar.visibility = View.GONE

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun sendOtp() {
        otpProgressBar.visibility = View.VISIBLE

        val email = emailEditText.text.toString().trim()

        if (email.isNotEmpty()) {
            val request = OtpRequest(email)

            // Make the API call to send OTP
            RetrofitClient.otpService.sendOtp(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    otpProgressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        // OTP sent successfully
                        Toast.makeText(this@OtpActivity, "OTP sent successfully!", Toast.LENGTH_SHORT).show()

                        // Show the OTP and password fields, hide the send OTP button
                        text2.visibility = View.VISIBLE
                        text3.visibility = View.VISIBLE
                        otp.visibility = View.VISIBLE
                        password.visibility = View.VISIBLE
                        resetPassword.visibility = View.VISIBLE
                        sendOtp.visibility = View.GONE // Hide send OTP button
                    } else {
                        otpProgressBar.visibility = View.GONE

                        Toast.makeText(this@OtpActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    otpProgressBar.visibility = View.GONE

                    // Handle network failure
                    Toast.makeText(this@OtpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            otpProgressBar.visibility = View.GONE

            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()

        }
    }

    private fun resetPassword(email: String, otp: String, newPassword: String) {
        resetPasswordProgressBar.visibility = View.VISIBLE

        val request = ResetPassword(email, otp, newPassword)

        // Make the API call to reset password
        RetrofitClient.resetPasswordService.resetPassword(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                resetPasswordProgressBar.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    // Password reset successfully, navigate to success page
                    val intent = Intent(this@OtpActivity, SuccessPageActivity::class.java)
                    startActivity(intent)
                    finish() // Close the current activity
                } else {
                    resetPasswordProgressBar.visibility = View.VISIBLE

                    Toast.makeText(this@OtpActivity, "Failed to reset password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                resetPasswordProgressBar.visibility = View.VISIBLE

                Toast.makeText(this@OtpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}