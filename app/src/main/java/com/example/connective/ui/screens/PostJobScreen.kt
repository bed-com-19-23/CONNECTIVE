package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    Column(
        Modifier.fillMaxSize().background(BackgroundLight)
            .verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Text("Employer Dashboard", color = TextGray, fontSize = 13.sp)
        Text("Post a New Job", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextDark)
        Spacer(Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Field("Job Title", title, "e.g. Marketing Officer") { title = it }
                Field("Company Name", company, "e.g. Sunrise Media") { company = it }

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

                Field("Location", location, "e.g. Blantyre, Malawi") { location = it }
                Field("Salary (Optional)", salary, "e.g. MWK 400,000 – 700,000") { salary = it }
                Field("Job Description", description, "Describe the role, responsibilities and requirements...", minLines = 4) { description = it }

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
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Post Job") }
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
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(10.dp)
    )
    Spacer(Modifier.height(12.dp))
}