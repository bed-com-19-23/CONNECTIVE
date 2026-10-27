package com.example.connective.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.AccountSuspendedException
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val registerState: StateFlow<UiState<User>> = _registerState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val resetPasswordState: StateFlow<UiState<String>> = _resetPasswordState.asStateFlow()

    fun login(email: String, pass: String) {
        val emailTrimmed = email.trim()
        val passwordTrimmed = pass.trim()

        if (emailTrimmed.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _loginState.value = UiState.Error("Please enter a valid email address.")
            return
        }
        if (passwordTrimmed.length < 8) {
            _loginState.value = UiState.Error("Password must be at least 8 characters.")
            return
        }

        _loginState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val user = ConnectiveRepository.loginUser(emailTrimmed, passwordTrimmed)
                _loginState.value = UiState.Success(user)
            } catch (e: AccountSuspendedException) {
                _loginState.value = UiState.Error(e.message ?: "Account suspended.")
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("user-not-found", ignoreCase = true) == true ||
                            e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                            e.message?.contains("wrong-password", ignoreCase = true) == true -> "Invalid email or password."
                    e.message?.contains("network", ignoreCase = true) == true -> "Network connection failed. Check internet."
                    else -> e.message ?: "Login failed. Please check credentials."
                }
                _loginState.value = UiState.Error(msg)
            }
        }
    }

    fun register(name: String, email: String, pass: String, confirmPass: String, role: String, companyName: String) {
        val nameTrimmed = name.trim()
        val emailTrimmed = email.trim()
        val passwordTrimmed = pass.trim()
        val sanitizedRole = role.lowercase().trim()

        if (nameTrimmed.isBlank()) {
            _registerState.value = UiState.Error("Full Name / Company Name is required.")
            return
        }
        if (emailTrimmed.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _registerState.value = UiState.Error("Please enter a valid email address.")
            return
        }
        if (passwordTrimmed.length < 8) {
            _registerState.value = UiState.Error("Password must be at least 8 characters.")
            return
        }
        if (passwordTrimmed != confirmPass.trim()) {
            _registerState.value = UiState.Error("Passwords do not match.")
            return
        }
        if (sanitizedRole == "admin") {
            _registerState.value = UiState.Error("Self-registration as administrator is prohibited.")
            return
        }

        _registerState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val user = ConnectiveRepository.registerUser(
                    name = nameTrimmed,
                    email = emailTrimmed,
                    password = passwordTrimmed,
                    role = sanitizedRole,
                    companyName = companyName.trim()
                )
                _registerState.value = UiState.Success(user)
            } catch (e: Exception) {
                val msg = when {
                    e.message?.contains("email-already-in-use", ignoreCase = true) == true ||
                            e.message?.contains("already in use", ignoreCase = true) == true -> "This email address is already in use by another account."
                    else -> e.message ?: "Registration failed."
                }
                _registerState.value = UiState.Error(msg)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        val emailTrimmed = email.trim()
        if (emailTrimmed.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            _resetPasswordState.value = UiState.Error("Please enter a valid email address.")
            return
        }

        _resetPasswordState.value = UiState.Loading
        viewModelScope.launch {
            try {
                ConnectiveRepository.sendPasswordResetEmail(emailTrimmed)
                _resetPasswordState.value = UiState.Success("Password reset link sent to $emailTrimmed. Please check your inbox.")
            } catch (e: Exception) {
                _resetPasswordState.value = UiState.Error(e.message ?: "Failed to send password reset email.")
            }
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
        _registerState.value = UiState.Idle
        _resetPasswordState.value = UiState.Idle
    }
}
