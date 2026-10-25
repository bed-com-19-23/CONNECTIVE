package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.theme.*

@Composable
fun PostJobScreen(onJobPosted: () -> Unit = {}) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf(ConnectiveRepository.currentUser?.companyName ?: "") }
    var type by remember { mutableStateOf("Full-time") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var minEducation by remember { mutableStateOf("Bachelor's Degree") }
    var minExperience by remember { mutableIntStateOf(2) }
    var description by remember { mutableStateOf("") }

    val educationLevels = listOf("Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School")
    val experienceOptions = listOf(0, 1, 2, 3, 5)

    val activeJobs = ConnectiveRepository.jobs

    Column(
        Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Centered Top Hover Bar Banner for Admin Job Control
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
                                imageVector = Icons.Default.Work,
                                contentDescription = "Admin Job Control",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Admin Job Control & Posting",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Create, publish, and manage active job listings (${activeJobs.size} Active)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Job Posting Form Card
        Text("Publish New Opportunity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Field("Job Title", title, "e.g. Software Developer") { title = it }
                Field("Company Name", company, "e.g. TechWave Ltd") { company = it }

                Text("Job Type", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Full-time", "Part-time", "Contract").forEach { t ->
                        val selected = t == type
                        Box(
                            Modifier
                                .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(20.dp))
                                .clickable { type = t }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) { Text(t, color = if (selected) Color.White else BluePrimary, fontSize = 13.sp) }
                    }
                }
                Spacer(Modifier.height(12.dp))

                Text("Required Qualification Criteria", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                Text("Minimum Required Education:", fontSize = 12.sp, color = TextGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    educationLevels.take(3).forEach { edu ->
                        val selected = edu == minEducation
                        Box(
                            Modifier
                                .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                .clickable { minEducation = edu }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    educationLevels.drop(3).forEach { edu ->
                        val selected = edu == minEducation
                        Box(
                            Modifier
                                .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                .clickable { minEducation = edu }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Minimum Required Experience:", fontSize = 12.sp, color = TextGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    experienceOptions.forEach { exp ->
                        val selected = exp == minExperience
                        Box(
                            Modifier
                                .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                .clickable { minExperience = exp }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text("$exp yr(s)+", fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                    }
                }
                Spacer(Modifier.height(12.dp))

                Field("Location", location, "e.g. Lilongwe, Malawi") { location = it }
                Field("Salary Range", salary, "e.g. MWK 600,000 – 1,000,000") { salary = it }
                Field("Job Description", description, "Describe the role, responsibilities and requirements...", minLines = 3) { description = it }

                Button(
                    onClick = {
                        if (title.isBlank() || location.isBlank()) {
                            Toast.makeText(context, "Please enter a title and location", Toast.LENGTH_SHORT).show()
                        } else {
                            ConnectiveRepository.addJob(
                                title = title,
                                company = company,
                                location = location,
                                type = type,
                                salary = salary,
                                minEducation = minEducation,
                                minExperienceYears = minExperience,
                                description = description
                            )
                            title = ""; location = ""; salary = ""; description = ""; type = "Full-time"
                            Toast.makeText(context, "Job posted successfully!", Toast.LENGTH_SHORT).show()
                            onJobPosted()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Post Job", fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Manage Existing Jobs Section
        Text("Manage Published Jobs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        Spacer(Modifier.height(10.dp))

        if (activeJobs.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    "No active job postings.",
                    modifier = Modifier.padding(16.dp),
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                activeJobs.forEach { job ->
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
                                Text("Min: ${job.minEducation} | ${job.minExperienceYears} yr(s)+", fontSize = 11.sp, color = BluePrimary)
                            }
                            IconButton(onClick = {
                                ConnectiveRepository.deleteJob(job.id)
                                Toast.makeText(context, "Job posting removed", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Job",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    hint: String,
    minLines: Int = 1,
    onChange: (String) -> Unit
) {
    Text(label, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(hint, fontSize = 13.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color(0xFFD0D7DE)
        ),
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(10.dp)
    )
    Spacer(Modifier.height(12.dp))
}
