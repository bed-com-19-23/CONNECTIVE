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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
        // Centered Top Hover Bar Banner: "Qualified candidates get hired faster"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        .padding(horizontal = 20.dp, vertical = 20.dp),
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
                                    imageVector = Icons.Default.Work,
                                    contentDescription = "Connective",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Qualified Candidates Get Hired Faster",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (isLoggedIn) "Jobs matched to your degree & experience" else "Sign in or create account to apply with your qualifications",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                        if (!isLoggedIn) {
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = onSignInClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Sign In", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
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
                        val activeGradient = Brush.horizontalGradient(listOf(BluePrimary, Color(0xFF00ACC1)))
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg)))
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
                            Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                        val activeGradient = Brush.horizontalGradient(listOf(BluePrimary, Color(0xFF00ACC1)))
                        Box(
                            Modifier
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg)))
                                .clickable { selectedFilter = f }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(f, color = if (selected) Color.White else BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
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
