package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.backend_api.RetrofitClient.getLogoutService
import com.example.myapplication.backend_api.SharedPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {

    private lateinit var cardView1: CardView
    private lateinit var cardView2: CardView
    private lateinit var cardView3: CardView
    private lateinit var cardView4: CardView
    private lateinit var btn: Button
    private var selectedCard: Int? = null
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var image: ImageView
    private lateinit var toolbar_text: TextView

    @SuppressLint("MissingInflatedId", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        window.statusBarColor = ContextCompat.getColor(this, R.color.button)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        cardView1 = findViewById(R.id.cardView1)
        cardView2 = findViewById(R.id.cardView2)
        cardView3 = findViewById(R.id.cardView3)
        cardView4 = findViewById(R.id.cardView4)
        btn = findViewById(R.id.btn_continue)
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        image = findViewById(R.id.image)
        toolbar_text = findViewById(R.id.toolbar_text)

        // Check if the user is admin or supervisor
        val isAdmin = SharedPreferencesHelper.getIsAdmin(this)
        val nameToShow = if (isAdmin) {
            SharedPreferencesHelper.getUsername(this) ?: "Admin" // Default to "Admin" if name is null
        } else {
            SharedPreferencesHelper.getSupervisorName(this) ?: "Supervisor" // Default to "Supervisor" if name is null
        }

        // Set the name to the TextView
        toolbar_text.text = "$nameToShow"


        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        btn.isEnabled = false

        // Set up CardView click listeners
        cardView1.setOnClickListener { onCardSelected(1) }
        cardView2.setOnClickListener { onCardSelected(2) }
        cardView3.setOnClickListener { onCardSelected(3) }
        cardView4.setOnClickListener { onCardSelected(4) }

        // Set up Button click listener
        btn.setOnClickListener {
            navigateToSelectedCard()
        }

    }

    private fun onCardSelected(cardId: Int) {
        selectedCard = cardId

        // Reset all card views to their default color
        resetCardViewColors()

        // Apply the animation to the selected card
        when (cardId) {
            1 -> {
                animateCardView(cardView1)
                cardView1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
            2 -> {
                animateCardView(cardView2)
                cardView2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
            3 -> {
                animateCardView(cardView3)
                cardView3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
            4 -> {
                animateCardView(cardView4)
                cardView4.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            }
        }

        // Enable the button
        btn.isEnabled = true
        // Change button color to its enabled color
        btn.setBackgroundColor(ContextCompat.getColor(this, R.color.button))
    }

    private fun navigateToSelectedCard() {
        val supervisorName = SharedPreferencesHelper.getSupervisorName(this)
        when (selectedCard) {
            1 -> startActivity(Intent(this, JobSetDetailsActivity::class.java).apply {
                putExtra("SUPERVISOR_NAME", supervisorName)
            })
            2 -> startActivity(Intent(this, JobSetReportActivity::class.java).apply {
                putExtra("SUPERVISOR_NAME", supervisorName)
            })
            3 -> startActivity(Intent(this, TimesheetActivity::class.java).apply {
                putExtra("SUPERVISOR_NAME", supervisorName)
            })
            4 -> startActivity(Intent(this, ReportActivity::class.java).apply {
                putExtra("SUPERVISOR_NAME", supervisorName)
            })
        }
    }

    // Function to reset all card views to their default color
    private fun resetCardViewColors() {
        cardView1.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        cardView2.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        cardView3.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        cardView4.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }

    // Function to animate CardView (scale down and then back to normal)
    private fun animateCardView(cardView: CardView) {
        // Scale down the card to 0.9 of its original size
        val scaleDown = ObjectAnimator.ofFloat(cardView, "scaleX", 0.9f)
        val scaleDownY = ObjectAnimator.ofFloat(cardView, "scaleY", 0.9f)

        // Scale back to the original size after a brief delay
        val scaleUp = ObjectAnimator.ofFloat(cardView, "scaleX", 1f)
        val scaleUpY = ObjectAnimator.ofFloat(cardView, "scaleY", 1f)

        // Set duration for shrinking and expanding
        scaleDown.duration = 200 // Shrink duration
        scaleDownY.duration = 200
        scaleUp.duration = 200 // Expand duration
        scaleUpY.duration = 200

        // Start shrinking
        scaleDown.start()
        scaleDownY.start()

        // Add a listener to perform scaling up after scaling down
        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Scale up the card after the shrinking animation ends
                scaleUp.start()
                scaleUpY.start()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_option, menu)

        val changePassword = menu?.findItem(R.id.changePassword)
        val logout = menu?.findItem(R.id.logout)
        val register = menu?.findItem(R.id.register)

        val isAdmin = SharedPreferencesHelper.getIsAdmin(this)

        // Debug: Log the value of isAdmin
        Log.d("HomeActivity", "is_admin from SharedPreferences: $isAdmin")

        // If isAdmin is false, hide the register option
        if (!isAdmin) {
            register?.isVisible = false
        } else {
            register?.isVisible = true
        }

        // Optional: Customize menu item titles
        val styledTitle1 = SpannableString("Change Password").apply {
            setSpan(ForegroundColorSpan(Color.BLACK), 0, length, 0)
            setSpan(RelativeSizeSpan(1.1f), 0, length, 0)
        }
        val styledTitle2 = SpannableString("Logout").apply {
            setSpan(ForegroundColorSpan(Color.BLACK), 0, length, 0)
            setSpan(RelativeSizeSpan(1.1f), 0, length, 0)
        }
        val styledTitle3 = SpannableString("Register").apply {
            setSpan(ForegroundColorSpan(Color.BLACK), 0, length, 0)
            setSpan(RelativeSizeSpan(1.1f), 0, length, 0)
        }

        changePassword?.title = styledTitle1
        logout?.title = styledTitle2
        register?.title = styledTitle3

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.changePassword -> {
                // Navigate to Change Password screen
                val intent = Intent(this, ChangePassword::class.java)
                startActivity(intent)
                true
            }
            R.id.register -> {
                // Navigate to Register screen
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                // Retrieve the access token
                val accessToken = SharedPreferencesHelper.getAccessToken(this)

                if (!accessToken.isNullOrEmpty()) {
                    // Show a confirmation dialog before logging out
                    AlertDialog.Builder(this).apply {
                        setTitle("Confirm Logout")
                        setMessage("Are you sure you want to log out?")

                        setPositiveButton("Yes") { _, _ ->
                            // Start a coroutine to call the suspend function
                            CoroutineScope(Dispatchers.Main).launch {
                                // Call the logout function here
                                val isLoggedOut = logoutFromServer(this@HomeActivity, accessToken)

                                if (isLoggedOut) {
                                    showToast("Logged out successfully")
                                    navigateToLoginScreen()
                                } else {
                                    showToast("Logout failed")
                                }
                            }
                        }

                        setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

                        create().show()
                    }
                } else {
                    // If no access token, redirect to the login screen
                    showToast("Access token not found. Please log in again.")
                    navigateToLoginScreen()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    suspend fun logoutFromServer(context: Context, accessToken: String): Boolean {
        return try {
            val logoutService = getLogoutService()
            val response = logoutService.logout("Bearer $accessToken")

            if (response.code() == 205) {
                // HTTP 205 was returned; handle as successful and ignore the body
                clearUserData(context)
                true
            } else if (response.isSuccessful) {
                clearUserData(context)
                true
            } else {
                Log.e("Logout", "Error: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: java.net.ProtocolException) {
            // Catch and handle the specific exception related to HTTP 205 with content length
            Log.e("Logout", "Protocol exception: ${e.message}")
            clearUserData(context) // Treat as a successful logout if necessary
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // Clear user data from SharedPreferences
    private fun clearUserData(context: Context) {
        // Clear tokens and other sensitive user data
        SharedPreferencesHelper.clearTokens(context)
        SharedPreferencesHelper.saveSupervisorName(context, "") // Clear supervisor name if stored
        SharedPreferencesHelper.saveIsAdmin(context, false) // Clear admin flag if stored
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // navigateToLoginScreen function
    fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish current activity to prevent back navigation to the Home screen
    }



}