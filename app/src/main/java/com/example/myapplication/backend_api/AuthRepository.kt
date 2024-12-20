package com.example.myapplication.backend_api

import android.content.Context
import com.example.myapplication.interface_api.AuthService
import com.example.myapplication.interface_api.RefreshTokenRequest
class AuthRepository(private val authService: AuthService) {

    suspend fun refreshToken(context: Context, refreshToken: String): Boolean {
        return try {
            // Making the API call to refresh the token
            val response = authService.refreshAccessToken(RefreshTokenRequest(refreshToken))

            // Check if the response is successful (status code 2xx)
            if (response.isSuccessful) {
                // Get the new access token from the response body
                val newAccessToken = response.body()?.access_token
                newAccessToken?.let {
                    // Save the new access token in SharedPreferences or another secure storage
                    SharedPreferencesHelper.saveAccessToken(context, it)
                    println("New access token: $it")
                }
                true // Refresh token was successful
            } else {
                // Log failure and response code for debugging
                println("Failed to refresh token: ${response.code()}")
                false // Refresh token failed
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., network failure)
            e.printStackTrace()
            false // Return false on exception
        }
    }
}
