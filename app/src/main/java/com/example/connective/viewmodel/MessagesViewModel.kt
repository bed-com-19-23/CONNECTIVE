package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ChatMessage
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.Conversation
import com.example.connective.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesViewModel : ViewModel() {

    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    val conversations: StateFlow<List<Conversation>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user != null) ConnectiveRepository.getUserConversationsFlow(user.uid)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMessages: StateFlow<List<ChatMessage>> = _selectedConversationId
        .flatMapLatest { convId ->
            if (!convId.isNullOrBlank()) ConnectiveRepository.getConversationMessagesFlow(convId)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectConversation(conversationId: String, currentUid: String) {
        _selectedConversationId.value = conversationId
        viewModelScope.launch {
            ConnectiveRepository.markConversationRead(conversationId, currentUid)
        }
    }

    fun startConversationWithUser(
        currentUser: User,
        partnerUid: String,
        partnerName: String,
        jobId: String? = null,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val convId = ConnectiveRepository.getOrCreateConversation(
                    myUid = currentUser.uid,
                    myName = currentUser.name,
                    partnerUid = partnerUid,
                    partnerName = partnerName,
                    jobId = jobId
                )
                _selectedConversationId.value = convId
                onCreated(convId)
            } catch (_: Exception) {}
        }
    }

    fun updateMessageInput(input: String) {
        _messageInput.value = input
    }

    fun sendMessage(currentUser: User, partnerUid: String) {
        val text = _messageInput.value.trim()
        val convId = _selectedConversationId.value
        if (text.isBlank() || convId.isNullOrBlank()) return

        _messageInput.value = ""
        viewModelScope.launch {
            try {
                ConnectiveRepository.sendChatMessage(
                    conversationId = convId,
                    senderId = currentUser.uid,
                    senderName = currentUser.name,
                    recipientId = partnerUid,
                    text = text
                )
            } catch (_: Exception) {}
        }
    }

    fun backToConversations() {
        _selectedConversationId.value = null
        _messageInput.value = ""
    }
}
