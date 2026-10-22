package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Search
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
fun CandidatesScreen() {
    var filter by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    val filters = listOf("All", "Applied", "Shortlisted", "Hired", "Rejected")

    val list = ConnectiveRepository.applications
        .filter { filter == "All" || it.status.equals(filter, ignoreCase = true) }
        .filter { it.candidateName.contains(query, ignoreCase = true) || it.jobTitle.contains(query, ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(BackgroundLight).padding(16.dp)) {
        Text("Candidates & Applicants", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search candidates or roles...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            filters.forEach { f ->
                val selected = f == filter
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .background(if (selected) BluePrimary else LightBlueBg, RoundedCornerShape(20.dp))
                        .clickable { filter = f }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) { Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium) }
            }
        }
        Spacer(Modifier.height(12.dp))
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No candidate applications found", color = TextGray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list) { c ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(44.dp).background(BluePrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(c.candidateName.firstOrNull()?.toString() ?: "C", color = Color.White, fontWeight = FontWeight.Bold) }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(c.candidateName, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text("${c.jobTitle} · ${c.company}", color = TextGray, fontSize = 12.sp)
                                    Text("${c.candidateEmail} ${if (c.candidatePhone.isNotBlank()) "· ${c.candidatePhone}" else ""}", color = TextGray, fontSize = 11.sp)
                                }
                                val statusColor = when (c.status) {
                                    "Shortlisted" -> Color(0xFF2E9E5B)
                                    "Hired" -> Color(0xFF7B3FE4)
                                    "Rejected" -> Color(0xFFD9534F)
                                    else -> BluePrimary
                                }
                                Text(
                                    c.status, color = statusColor, fontSize = 11.sp,
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(10.dp)) {
                                    Text("Qualification: ${c.candidateEducation} · ${c.candidateExperienceYears} yr(s) exp", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                                    if (c.candidateSkills.isNotBlank()) {
                                        Text("Skills: ${c.candidateSkills}", fontSize = 11.sp, color = TextGray)
                                    }
                                    if (c.coverNote.isNotBlank()) {
                                        Text("Note: ${c.coverNote}", fontSize = 11.sp, color = TextGray)
                                    }
                                    if (c.attachments.isNotEmpty()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text("Attachments (${c.attachments.size}):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                        c.attachments.forEach { file ->
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                                                Icon(Icons.Default.AttachFile, null, tint = BluePrimary, modifier = Modifier.size(12.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text(file, fontSize = 11.sp, color = TextDark)
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { ConnectiveRepository.updateApplicationStatus(c.id, "Shortlisted") }) {
                                    Text("Shortlist", fontSize = 12.sp, color = Color(0xFF2E9E5B))
                                }
                                TextButton(onClick = { ConnectiveRepository.updateApplicationStatus(c.id, "Hired") }) {
                                    Text("Hire", fontSize = 12.sp, color = Color(0xFF7B3FE4))
                                }
                                TextButton(onClick = { ConnectiveRepository.updateApplicationStatus(c.id, "Rejected") }) {
                                    Text("Reject", fontSize = 12.sp, color = Color(0xFFD9534F))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}