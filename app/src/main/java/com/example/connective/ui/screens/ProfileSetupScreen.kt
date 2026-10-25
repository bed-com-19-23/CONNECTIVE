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
import com.example.connective.ui.components.AuthField
import com.example.connective.ui.theme.*

@Composable
fun ProfileSetupScreen(onCompleted: () -> Unit) {
    val context = LocalContext.current
    val user = ConnectiveRepository.currentUser

    var name by remember { mutableStateOf(user?.name ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var location by remember { mutableStateOf(user?.location ?: "") }
    var title by remember { mutableStateOf(user?.title ?: "") }
    var educationLevel by remember { mutableStateOf(user?.educationLevel ?: "Bachelor's Degree") }
    var experienceYears by remember { mutableIntStateOf(user?.experienceYears ?: 2) }
    var skills by remember { mutableStateOf(user?.skills ?: "") }
    var bio by remember { mutableStateOf(user?.bio ?: "") }

    val educationLevels = listOf("Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School")
    val experienceOptions = listOf(0, 1, 2, 3, 5)

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Complete Your Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Provide your details & qualifications so job applications match your profile and employers can reach you easily.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(20.dp))

                    Text("Personal & Contact Info", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
                    Spacer(Modifier.height(10.dp))

                    AuthField("Full Name", name, { name = it }, "e.g. Shepherd Sadick")
                    AuthField("Phone Number", phone, { phone = it }, "e.g. +265 99 123 4567")
                    AuthField("Current Location", location, { location = it }, "e.g. Lilongwe, Malawi")
                    AuthField("Professional Title / Field", title, { title = it }, "e.g. Software Developer / Accountant")

                    Spacer(Modifier.height(12.dp))
                    Text("Qualifications & Experience", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
                    Spacer(Modifier.height(10.dp))

                    Text("Highest Education Level:", fontSize = 13.sp, color = TextGray)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        educationLevels.take(3).forEach { edu ->
                            val selected = edu == educationLevel
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { educationLevel = edu }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        educationLevels.drop(3).forEach { edu ->
                            val selected = edu == educationLevel
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { educationLevel = edu }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Years of Relevant Experience:", fontSize = 13.sp, color = TextGray)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        experienceOptions.forEach { exp ->
                            val selected = exp == experienceYears
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { experienceYears = exp }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("$exp yr(s)", fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    AuthField("Key Skills", skills, { skills = it }, "e.g. Kotlin, Accounting, Digital Marketing")

                    Text("Short Summary / Bio", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        placeholder = { Text("Brief introduction about your experience and career goals...", fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank() || location.isBlank()) {
                                Toast.makeText(context, "Please enter your name, phone and location", Toast.LENGTH_SHORT).show()
                            } else {
                                ConnectiveRepository.updateUserProfileDetails(
                                    name = name,
                                    phone = phone,
                                    location = location,
                                    title = title,
                                    educationLevel = educationLevel,
                                    experienceYears = experienceYears,
                                    skills = skills,
                                    bio = bio
                                )
                                Toast.makeText(context, "Profile setup complete!", Toast.LENGTH_SHORT).show()
                                onCompleted()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Save & Continue to Applications") }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}