package com.example.myapplication.backend_api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(authRepository) as T
    }
}



