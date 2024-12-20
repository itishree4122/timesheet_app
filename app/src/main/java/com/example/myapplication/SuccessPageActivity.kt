package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SuccessPageActivity : AppCompatActivity() {

    private lateinit var gotoLogin: TextView
    private lateinit var checkMark: ImageView
    private lateinit var successMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_success_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkMark = findViewById(R.id.successIcon)
        successMsg = findViewById(R.id.successMessage)
        gotoLogin = findViewById(R.id.linkToLogin)
        gotoLogin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)

            finish() // Close the success page
        }

        // Start animation after a short delay
        animateViews()
    }

    private fun animateViews() {
        // First animate the success icon
        checkMark.apply {
            visibility = View.VISIBLE
            alpha = 0f // Set initial alpha to 0
            animate()
                .alpha(1f) // Animate to full opacity
                .setDuration(500) // Duration of the animation
                .withEndAction {
                    // After the icon is animated, animate the success message
                    successMsg.apply {
                        visibility = View.VISIBLE
                        alpha = 0f // Set initial alpha to 0
                        animate()
                            .alpha(1f)
                            .setDuration(500) // Duration of the animation
                            .withEndAction {
                                // After the success message is animated, animate the link
                                gotoLogin.apply {
                                    visibility = View.VISIBLE
                                    alpha = 0f // Set initial alpha to 0
                                    animate()
                                        .alpha(1f)
                                        .setDuration(500) // Duration of the animation
                                }
                            }
                    }
                }
        }
    }
}