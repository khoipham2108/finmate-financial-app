package com.example.finmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finmate.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.currentUser != null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, pass)
            if (result.isSuccess) {
                _isLoggedIn.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
            _isLoading.value = false
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.register(email, pass)
            if (result.isSuccess) {
                _isLoggedIn.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        repository.logout()
        _isLoggedIn.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
