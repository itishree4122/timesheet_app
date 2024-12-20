package com.example.myapplication.backend_api

import android.content.Context
import android.widget.Toast
import com.example.myapplication.interface_api.RefreshTokenRequest
import com.example.myapplication.interface_api.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenManager {

//    fun refreshAccessToken(context: Context, refreshToken: String) {
//        val apiService = RetrofitClient.authService
//        val refreshRequest = RefreshTokenRequest(refresh = refreshToken)
//
//        apiService.refreshAccessToken(refreshRequest).enqueue(object : Callback<Token> {
//            override fun onResponse(call: Call<Token>, response: Response<Token>) {
//                if (response.isSuccessful) {
//                    val newAccessToken = response.body()?.access
//                    val newRefreshToken = response.body()?.refresh
//
//                    newAccessToken?.let {
//                        SharedPreferencesHelper.saveAccessToken(context, it)
//                        Toast.makeText(context, "Access token refreshed", Toast.LENGTH_SHORT).show()
//                    }
//
//                    newRefreshToken?.let {
//                        SharedPreferencesHelper.saveRefreshToken(context, it)
//                    }
//                } else {
//                    Toast.makeText(context, "Failed to refresh token: ${response.message()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Token>, t: Throwable) {
//                Toast.makeText(context, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}
