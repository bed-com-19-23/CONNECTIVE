package com.example.connective.data

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectiveMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            ConnectiveRepository.updateFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Connective Notification"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        if (body.isNotBlank()) {
            NotificationHelper.showNotification(applicationContext, title, body)
        }
    }
}
