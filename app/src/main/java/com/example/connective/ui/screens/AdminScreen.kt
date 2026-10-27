package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.Job
import com.example.connective.data.User
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.AdminViewModel

@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.adminTab.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val users by viewModel.filteredUsers.collectAsState()
    val jobs by viewModel.filteredJobs.collectAsState()
    val metrics by viewModel.platformMetrics.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Admin Header Banner
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
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Admin Control Dashboard",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "System Administration & Platform Metrics",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Tab Selector Row: Users | Jobs | Reports
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Users", "Jobs", "Reports").forEach { tab ->
                val isSelected = tab == currentTab
                val activeGradient = Brush.horizontalGradient(listOf(BluePrimary, Color(0xFF00ACC1)))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg))
                        )
                        .clickable {
                            viewModel.setAdminTab(tab)
                            viewModel.setSearchQuery("")
                        }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else BluePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (currentTab != "Reports") {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search $currentTab...", fontSize = 13.sp) },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        when (currentTab) {
            "Users" -> {
                if (users.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No user accounts found", color = TextGray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(users, key = { it.uid }) { user ->
                            AdminUserCard(
                                user = user,
                                onToggleStatus = {
                                    viewModel.toggleUserStatus(user)
                                    val action = if (user.status == "active") "suspended" else "reactivated"
                                    Toast.makeText(context, "User ${user.name} $action", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = {
                                    viewModel.deleteUser(user.uid)
                                    Toast.makeText(context, "User ${user.name} deleted", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            "Jobs" -> {
                if (jobs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No active job postings found", color = TextGray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(jobs, key = { it.id }) { job ->
                            AdminJobCard(
                                job = job,
                                onDelete = {
                                    viewModel.deleteJob(job.id)
                                    Toast.makeText(context, "Job posting removed", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }

            "Reports" -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Platform Totals & Activity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextNavy)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Total Users",
                            count = metrics.first.toString(),
                            icon = Icons.Default.People,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Open Jobs",
                            count = metrics.second.toString(),
                            icon = Icons.Default.Work,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Applications",
                            count = metrics.third.toString(),
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUserCard(
    user: User,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val isSuspended = user.status.equals("suspended", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSuspended) Color.Red.copy(alpha = 0.15f) else Color(0xFF2E9E5B).copy(alpha = 0.15f),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isSuspended) "SUSPENDED" else "ACTIVE",
                                color = if (isSuspended) Color.Red else Color(0xFF2E9E5B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text("Email: ${user.email}", fontSize = 12.sp, color = TextGray)
                    Text("Role: ${user.role.uppercase()} • ${user.title.ifBlank { user.companyName }}", fontSize = 11.sp, color = BluePrimary)
                }

                if (user.role != "admin") {
                    Row {
                        IconButton(onClick = onToggleStatus) {
                            Icon(
                                imageVector = if (isSuspended) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = "Toggle status",
                                tint = if (isSuspended) Color(0xFF2E9E5B) else Color.Red
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete user",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminJobCard(
    job: Job,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Text("${job.company} • ${job.location}", fontSize = 12.sp, color = TextGray)
                Text("Category: ${job.category} | Min: ${job.minEducation}", fontSize = 11.sp, color = BluePrimary)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Job",
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(LightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BluePrimary)
            }
            Spacer(Modifier.height(8.dp))
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            Text(title, fontSize = 12.sp, color = TextGray)
        }
    }
}
