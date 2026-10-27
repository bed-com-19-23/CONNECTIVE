package com.example.connective.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.JobApplication
import com.example.connective.data.User
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.CandidatesViewModel

@Composable
fun CandidatesScreen(
    onStartConversation: (partnerUid: String, partnerName: String, jobId: String?) -> Unit,
    viewModel: CandidatesViewModel
) {
    val context = LocalContext.current

    val searchMode by viewModel.searchMode.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val locationFilter by viewModel.locationFilter.collectAsState()
    val minExp by viewModel.minExperienceYears.collectAsState()

    val applications by viewModel.filteredApplications.collectAsState()
    val searchedSeekers by viewModel.searchedSeekers.collectAsState()

    val modes = listOf("Applications", "Find Candidates")
    val statusFilters = listOf("All", "Applied", "Shortlisted", "Rejected", "Hired")

    Column(
        Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        Text("Candidate Management", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark)
        Spacer(Modifier.height(10.dp))

        // Mode Switcher: Applications | Find Candidates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            modes.forEach { mode ->
                val isSelected = mode == searchMode
                val activeGradient = Brush.horizontalGradient(listOf(BluePrimary, Color(0xFF00ACC1)))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg))
                        )
                        .clickable { viewModel.setSearchMode(mode) }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = mode,
                        color = if (isSelected) Color.White else BluePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (searchMode == "Applications") {
            // Status Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                statusFilters.forEach { f ->
                    val selected = f == statusFilter
                    Box(
                        Modifier
                            .padding(horizontal = 3.dp)
                            .background(if (selected) BluePrimary else LightBlueBg, RoundedCornerShape(20.dp))
                            .clickable { viewModel.setStatusFilter(f) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (applications.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No candidate applications found", color = TextGray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(applications, key = { it.id }) { c ->
                        CandidateApplicationCard(
                            application = c,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateStatus(c, newStatus)
                                Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                            },
                            onMessageClick = {
                                onStartConversation(c.seekerId, c.candidateName, c.jobId)
                            }
                        )
                    }
                }
            }
        } else {
            // "Find Candidates" Search Mode
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search skills, name, or title...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = Color(0xFFD0D7DE)
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = locationFilter,
                    onValueChange = { viewModel.setLocationFilter(it) },
                    placeholder = { Text("City / Location", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Button(
                    onClick = {
                        val nextExp = if (minExp >= 5) 0 else minExp + 1
                        viewModel.setMinExperienceYears(nextExp)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LightBlueBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("$minExp+ yr(s) exp", color = BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(10.dp))

            if (searchedSeekers.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No candidate profiles match your search criteria", color = TextGray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(searchedSeekers, key = { it.uid }) { seeker ->
                        SeekerProfileCard(
                            seeker = seeker,
                            onMessageClick = {
                                onStartConversation(seeker.uid, seeker.name, null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CandidateApplicationCard(
    application: JobApplication,
    onUpdateStatus: (String) -> Unit,
    onMessageClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(BluePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        application.candidateName.firstOrNull()?.toString() ?: "C",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(application.candidateName, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${application.jobTitle} · ${application.company}", color = TextGray, fontSize = 12.sp)
                    Text(
                        "${application.candidateEmail} ${if (application.candidatePhone.isNotBlank()) "· ${application.candidatePhone}" else ""}",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
                val statusColor = when (application.status) {
                    "Shortlisted" -> Color(0xFF2E9E5B)
                    "Hired" -> Color(0xFF7B3FE4)
                    "Rejected" -> Color(0xFFD9534F)
                    else -> BluePrimary
                }
                Text(
                    application.status,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
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
                    Text(
                        "Qualification: ${application.candidateEducation} · ${application.candidateExperienceYears} yr(s) exp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextDark
                    )
                    if (application.candidateSkills.isNotBlank()) {
                        Text("Skills: ${application.candidateSkills}", fontSize = 11.sp, color = TextGray)
                    }
                    if (application.coverNote.isNotBlank()) {
                        Text("Note: ${application.coverNote}", fontSize = 11.sp, color = TextGray)
                    }
                    if (application.attachments.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Attached Documents (${application.attachments.size}):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        application.attachments.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachFile, null, tint = BluePrimary, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(item.name, fontSize = 11.sp, color = TextDark)
                                }
                                if (item.url.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Cannot open file URL", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, "Open Document", tint = BluePrimary, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onMessageClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = BluePrimary)
                    Spacer(Modifier.width(4.dp))
                    Text("Message", fontSize = 11.sp, color = BluePrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { onUpdateStatus("Shortlisted") }) {
                        Text("Shortlist", fontSize = 12.sp, color = Color(0xFF2E9E5B), fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { onUpdateStatus("Hired") }) {
                        Text("Hire", fontSize = 12.sp, color = Color(0xFF7B3FE4), fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { onUpdateStatus("Rejected") }) {
                        Text("Reject", fontSize = 12.sp, color = Color(0xFFD9534F), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SeekerProfileCard(
    seeker: User,
    onMessageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(BluePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        seeker.avatarInitials.ifBlank { seeker.name.firstOrNull()?.toString() ?: "S" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(seeker.name, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(seeker.title, color = BluePrimary, fontSize = 12.sp)
                    Text("${seeker.location} · ${seeker.educationLevel} · ${seeker.experienceYears} yr(s) exp", color = TextGray, fontSize = 11.sp)
                }
                Button(
                    onClick = onMessageClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Message", fontSize = 11.sp, color = Color.White)
                }
            }

            if (seeker.skills.isNotBlank() || seeker.bio.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = BackgroundLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(10.dp)) {
                        if (seeker.skills.isNotBlank()) {
                            Text("Skills: ${seeker.skills}", fontSize = 11.sp, color = TextDark, fontWeight = FontWeight.Medium)
                        }
                        if (seeker.bio.isNotBlank()) {
                            Text("Bio: ${seeker.bio}", fontSize = 11.sp, color = TextGray)
                        }
                    }
                }
            }
        }
    }
}
