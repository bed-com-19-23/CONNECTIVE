package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.theme.*

@Composable
fun MessagesScreen() {
    val messages = ConnectiveRepository.messages
    var input by remember { mutableStateOf("") }
    val currentUser = ConnectiveRepository.currentUser

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                if (currentUser?.role == "employer") "Candidate Inquiry (Brian Moyo)" else "Employer Support (Sunrise Media)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )
            Text("Active Chat", color = Color(0xFF2E9E5B), fontSize = 12.sp)
        }
        HorizontalDivider()
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { m ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (m.mine) Arrangement.End else Arrangement.Start
                ) {
                    Column(horizontalAlignment = if (m.mine) Alignment.End else Alignment.Start) {
                        Text(
                            m.senderName,
                            fontSize = 10.sp,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Text(
                            m.text,
                            color = if (m.mine) Color.White else TextDark,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .widthIn(max = 260.dp)
                                .background(
                                    if (m.mine) BluePrimary else BackgroundLight,
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (input.isNotBlank()) {
                    ConnectiveRepository.sendMessage(input)
                    input = ""
                }
            }) { Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = BluePrimary) }
        }
    }
}