package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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

@Composable
fun ApplicationsScreen() {
    var filter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Applied", "Shortlisted", "Rejected")

    val currentUser = ConnectiveRepository.currentUser
    val applications = ConnectiveRepository.applications.filter { app ->
        val matchesUser = currentUser == null || app.candidateEmail == currentUser.email || currentUser.role == "employer"
        val matchesFilter = filter == "All" || app.status.equals(filter, ignoreCase = true)
        matchesUser && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // Centered Top Hover Bar Banner for Applications with Vibrant Colors
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
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = "Applications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Applications",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Track your job applications and status (${applications.size} Total)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Filter Bar with Vibrant Gradient Hover Colors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            filters.forEach { f ->
                val selected = f == filter
                val activeGradient = Brush.horizontalGradient(
                    listOf(BluePrimary, Color(0xFF00ACC1))
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (selected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg))
                        )
                        .clickable { filter = f }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = f,
                        color = if (selected) Color.White else BluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (applications.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No applications found", color = TextGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(applications) { app ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(44.dp)
                                    .background(BluePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    app.candidateName.firstOrNull()?.toString() ?: "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(app.jobTitle, fontWeight = FontWeight.Bold, color = TextDark)
                                Text(app.company, color = TextGray, fontSize = 12.sp)
                                Text("Applied: ${app.appliedDate}", color = TextGray, fontSize = 11.sp)
                            }
                            val statusColor = when (app.status) {
                                "Shortlisted" -> Color(0xFF2E9E5B)
                                "Hired" -> Color(0xFF7B3FE4)
                                "Rejected" -> Color(0xFFD9534F)
                                else -> BluePrimary
                            }
                            Text(
                                app.status,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
