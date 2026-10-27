package com.example.connective.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.AppNotification
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.User
import com.example.connective.data.db.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = SessionManager(application)

    init {
        ConnectiveRepository.initialize(application)
    }

    val currentUser: StateFlow<User?> = ConnectiveRepository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notifications: StateFlow<List<AppNotification>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user != null) ConnectiveRepository.getNotificationsFlow(user.uid)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun logout() {
        ConnectiveRepository.logout()
    }
}
