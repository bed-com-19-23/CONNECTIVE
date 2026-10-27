package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkUnreadChatAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ChatMessage
import com.example.connective.data.Conversation
import com.example.connective.data.TimeUtils
import com.example.connective.data.User
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.MessagesViewModel

@Composable
fun MessagesScreen(
    currentUser: User?,
    viewModel: MessagesViewModel
) {
    if (currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .size(90.dp)
                    .background(LightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = BluePrimary, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Messaging Support", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextNavy)
            Spacer(Modifier.height(8.dp))
            Text(
                "Please sign in to view active chats, send messages, and receive real-time application updates.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val selectedConvId by viewModel.selectedConversationId.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()

    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(activeMessages.size) {
        if (activeMessages.isNotEmpty()) {
            listState.animateScrollToItem(activeMessages.size - 1)
        }
    }

    if (selectedConvId.isNullOrBlank()) {
        // Conversation List Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(16.dp)
        ) {
            // Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    BluePrimary,
                                    Color(0xFF3F51B5),
                                    Color(0xFF00ACC1)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Messages",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Conversations",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Real-time communication with users (${conversations.size} Active)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            if (conversations.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MarkUnreadChatAlt,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No active conversations yet",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(conversations, key = { it.id }) { conv ->
                        ConversationCard(
                            conversation = conv,
                            currentUid = currentUser.uid,
                            onClick = {
                                viewModel.selectConversation(conv.id, currentUser.uid)
                            }
                        )
                    }
                }
            }
        }
    } else {
        // Chat Messages Screen
        val currentConv = conversations.find { it.id == selectedConvId }
        val partnerUid = currentConv?.participantIds?.find { it != currentUser.uid } ?: ""
        val partnerName = currentConv?.participantNames?.get(partnerUid) ?: "User"

        Column(Modifier.fillMaxSize().background(BackgroundLight)) {
            // Header Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.backToConversations() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDark)
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(38.dp)
                            .background(BluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            partnerName.firstOrNull()?.toString() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(partnerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text("Active Chat", fontSize = 11.sp, color = Color(0xFF2E9E5B))
                    }
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(activeMessages, key = { it.id }) { m ->
                    val isMine = m.senderId == currentUser.uid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                    ) {
                        Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {
                            Text(
                                text = "${m.senderName} • ${TimeUtils.formatRelativeTime(m.createdAt)}",
                                fontSize = 10.sp,
                                color = TextGray,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMine) 16.dp else 2.dp,
                                    bottomEnd = if (isMine) 2.dp else 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMine) BluePrimary else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Text(
                                    text = m.text,
                                    color = if (isMine) Color.White else TextDark,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .widthIn(max = 270.dp)
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Input Field Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { viewModel.updateMessageInput(it) },
                        placeholder = { Text("Type a message...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(currentUser, partnerUid)
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BluePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    currentUid: String,
    onClick: () -> Unit
) {
    val partnerUid = conversation.participantIds.find { it != currentUid } ?: ""
    val partnerName = conversation.participantNames[partnerUid] ?: "User"
    val isUnread = conversation.unreadBy.contains(currentUid)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isUnread) LightBlueBg.copy(alpha = 0.5f) else Color.White),
        elevation = CardDefaults.cardElevation(if (isUnread) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    partnerName.firstOrNull()?.toString() ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        partnerName,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                        color = TextDark,
                        fontSize = 15.sp
                    )
                    Text(
                        TimeUtils.formatRelativeTime(conversation.lastMessageAt),
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        conversation.lastMessage,
                        color = if (isUnread) TextDark else TextGray,
                        fontSize = 13.sp,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    if (isUnread) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BluePrimary, CircleShape)
                        )
                    }
                }
            }
        }
    }
}
