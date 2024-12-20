package com.example.myapplication.backend_api

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun refreshToken(context: Context, refreshToken: String) {
        viewModelScope.launch {
            val success = authRepository.refreshToken(context, refreshToken)
            if (success) {
                // Access token refreshed successfully, update UI if needed
                println("Access token refreshed successfully")
            } else {
                // Handle failure, such as showing an error message or logging out the user
                println("Failed to refresh access token")
            }
        }
    }
}
