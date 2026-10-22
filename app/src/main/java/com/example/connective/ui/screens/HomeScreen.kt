package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.components.JobCard
import com.example.connective.ui.theme.*

@Composable
fun HomeScreen(
    isLoggedIn: Boolean = false,
    onSignInClick: () -> Unit = {},
    onJobClick: (Int) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "My Qualification", "Bachelor's", "Diploma", "Certificate", "Full-time")

    val currentUser = ConnectiveRepository.currentUser

    val filteredJobs = ConnectiveRepository.jobs.filter { job ->
        val matchesQuery = job.title.contains(searchQuery, ignoreCase = true) ||
                job.company.contains(searchQuery, ignoreCase = true) ||
                job.location.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "All" -> true
            "My Qualification" -> {
                if (currentUser != null) {
                    val userRank = ConnectiveRepository.getEducationRank(currentUser.educationLevel)
                    val jobRank = ConnectiveRepository.getEducationRank(job.minEducation)
                    userRank >= jobRank
                } else true
            }
            "Bachelor's" -> job.minEducation.contains("Bachelor", ignoreCase = true)
            "Diploma" -> job.minEducation.contains("Diploma", ignoreCase = true)
            "Certificate" -> job.minEducation.contains("Certificate", ignoreCase = true)
            "Full-time" -> job.type.equals("Full-time", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Connective", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = BluePrimary)
                    Text("Jobs matched by qualification", fontSize = 13.sp, color = TextGray)
                }
                if (!isLoggedIn) {
                    Button(
                        onClick = onSignInClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Sign In", fontSize = 13.sp, color = Color.White)
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search jobs, qualifications, companies...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, "Clear search")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filters.take(3).forEach { f ->
                        val selected = f == selectedFilter
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .background(if (selected) BluePrimary else LightBlueBg, RoundedCornerShape(20.dp))
                                .clickable {
                                    if (f == "My Qualification" && !isLoggedIn) {
                                        Toast.makeText(context, "Please sign in to filter by your qualification", Toast.LENGTH_SHORT).show()
                                        onSignInClick()
                                    } else {
                                        selectedFilter = f
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filters.drop(3).forEach { f ->
                        val selected = f == selectedFilter
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .background(if (selected) BluePrimary else LightBlueBg, RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = f }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(HeroBannerGradient, RoundedCornerShape(16.dp))
                    .padding(18.dp)
                    .then(if (!isLoggedIn) Modifier.clickable { onSignInClick() } else Modifier)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Qualified candidates get\nhired faster",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isLoggedIn) "Filter jobs matching your degree & experience" else "Sign in or Create Account to apply with your qualifications →",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (searchQuery.isNotEmpty() || selectedFilter != "All") "Filtered Jobs (${filteredJobs.size})" else "Featured Jobs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextNavy
                )
            }
        }
        items(filteredJobs) { job ->
            JobCard(
                job = job,
                onSaveClick = {
                    if (isLoggedIn) {
                        ConnectiveRepository.toggleSaveJob(job.id)
                    } else {
                        Toast.makeText(context, "Please sign in to save jobs", Toast.LENGTH_SHORT).show()
                        onSignInClick()
                    }
                },
                onClick = { onJobClick(job.id) }
            )
        }
    }
}