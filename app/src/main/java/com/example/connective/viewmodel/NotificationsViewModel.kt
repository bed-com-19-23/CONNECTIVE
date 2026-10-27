package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.AppNotification
import com.example.connective.data.ConnectiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModel : ViewModel() {

    val notifications: StateFlow<List<AppNotification>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user != null) ConnectiveRepository.getNotificationsFlow(user.uid)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markRead(notificationId: String) {
        val uid = ConnectiveRepository.currentAuthUid ?: return
        viewModelScope.launch {
            ConnectiveRepository.markNotificationRead(uid, notificationId)
        }
    }

    fun markAllRead() {
        val uid = ConnectiveRepository.currentAuthUid ?: return
        viewModelScope.launch {
            ConnectiveRepository.markAllNotificationsRead(uid)
        }
    }
}
