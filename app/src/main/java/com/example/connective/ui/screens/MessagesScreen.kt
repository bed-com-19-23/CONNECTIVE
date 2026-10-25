package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
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
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen() {
    val currentUser = ConnectiveRepository.currentUser
    val messages = ConnectiveRepository.messages
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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

    val currentUserName = currentUser.name
    val userFirstName = currentUserName.split(" ").firstOrNull() ?: currentUserName

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        // Centered Top Hover Bar Header with User's Name
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
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
                            text = "Connective HR Support",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00E676), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Hello, $currentUserName (Online)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
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
            items(messages) { m ->
                val displaySenderName = if (m.mine) currentUserName else m.senderName
                val displayText = m.text
                    .replace("Brian Moyo", currentUserName)
                    .replace("Brian", userFirstName)
                    .replace("Applicant", currentUserName)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
                ) {
                    Column(horizontalAlignment = if (m.mine) Alignment.End else Alignment.Start) {
                        Text(
                            text = "$displaySenderName • ${m.timestamp}",
                            fontSize = 10.sp,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (m.mine) 16.dp else 2.dp,
                                bottomEnd = if (m.mine) 2.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (m.mine) BluePrimary else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Text(
                                text = displayText,
                                color = if (m.mine) Color.White else TextDark,
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
                    value = input,
                    onValueChange = { input = it },
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
                        val textToSend = input.trim()
                        if (textToSend.isNotBlank()) {
                            input = ""
                            ConnectiveRepository.sendMessage(textToSend)

                            // Well-triggered automated response addressing the app owner by name
                            coroutineScope.launch {
                                delay(1000)
                                ConnectiveRepository.triggerConditionResponse(currentUser)
                            }
                        }
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
