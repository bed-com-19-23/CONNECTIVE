package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _updateState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val updateState: StateFlow<UiState<String>> = _updateState.asStateFlow()

    val currentUser: StateFlow<User?> = ConnectiveRepository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateProfile(updatedUser: User) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            try {
                ConnectiveRepository.updateUserProfile(updatedUser)
                _updateState.value = UiState.Success("Profile updated successfully!")
            } catch (e: Exception) {
                _updateState.value = UiState.Error(e.message ?: "Failed to update profile.")
            }
        }
    }

    fun resetState() {
        _updateState.value = UiState.Idle
    }
}
