package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
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
import com.example.connective.data.User
import com.example.connective.ui.components.JobCard
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    currentUser: User?,
    unreadNotifCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onJobClick: (String) -> Unit,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filteredJobs by viewModel.filteredJobs.collectAsState()
    val savedJobIds by viewModel.savedJobIds.collectAsState()

    val categories = listOf("All", "IT", "Finance", "Marketing", "Design", "Healthcare", "Education", "Engineering", "Other")
    val filters = listOf("All", "My Qualification", "Bachelor's", "Diploma", "Certificate", "Full-time")

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
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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

                            Text(
                                text = "Qualified Candidates Get Hired Faster",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            )

                            // Bell Icon with Unread Badge
                            Box {
                                IconButton(onClick = onNotificationsClick) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }
                                if (unreadNotifCount > 0) {
                                    Badge(
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Text("$unreadNotifCount")
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (currentUser != null) "Jobs matched to your degree & experience" else "Sign in or create account to apply with your qualifications",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )

                        if (currentUser == null) {
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

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search jobs, qualifications, companies...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, "Clear search")
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
        }

        // Category Filter Chips
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Categories", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextNavy)
                Spacer(Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val selected = cat == selectedCategory
                        val activeGradient = Brush.horizontalGradient(listOf(BluePrimary, Color(0xFF00ACC1)))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) activeGradient else Brush.linearGradient(listOf(LightBlueBg, LightBlueBg)))
                                .clickable { viewModel.updateCategory(cat) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(cat, color = if (selected) Color.White else BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Qualification & Type Filters
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
                                    if (f == "My Qualification" && currentUser == null) {
                                        Toast.makeText(context, "Please sign in to filter by your qualification", Toast.LENGTH_SHORT).show()
                                        onSignInClick()
                                    } else {
                                        viewModel.updateFilter(f)
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
                                .clickable { viewModel.updateFilter(f) }
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
                    if (searchQuery.isNotEmpty() || selectedFilter != "All" || selectedCategory != "All")
                        "Filtered Jobs (${filteredJobs.size})"
                    else "Featured Opportunities (${filteredJobs.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextNavy
                )
            }
        }

        if (filteredJobs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "No job opportunities match your search or filter criteria.",
                        modifier = Modifier.padding(20.dp),
                        color = TextGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredJobs, key = { it.id }) { job ->
                val isSaved = savedJobIds.contains(job.id)
                JobCard(
                    job = job,
                    isSaved = isSaved,
                    onSaveClick = {
                        if (currentUser != null) {
                            viewModel.toggleSaveJob(job.id, currentUser)
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
}
