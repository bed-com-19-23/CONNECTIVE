package com.example.connective.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
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
import com.example.connective.data.QualificationResult
import com.example.connective.ui.theme.*

@Composable
fun JobDetailsScreen(
    jobId: Int,
    isLoggedIn: Boolean = false,
    onRequireAuth: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val job = ConnectiveRepository.jobs.find { it.id == jobId } ?: ConnectiveRepository.jobs.first()
    val user = ConnectiveRepository.currentUser

    var isApplying by remember { mutableStateOf(false) }
    var ineligibleReason by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    if (isApplying && user != null) {
        JobApplicationFormScreen(
            jobTitle = job.title,
            company = job.company,
            minEducation = job.minEducation,
            minExperience = job.minExperienceYears,
            initialName = user.name,
            initialEmail = user.email,
            initialPhone = user.phone,
            initialLocation = user.location,
            initialEducation = user.educationLevel,
            initialExperience = user.experienceYears,
            onBack = { isApplying = false },
            onSubmit = { name, email, phone, loc, edu, exp, skills, cover, files ->
                val result = ConnectiveRepository.applyForJobWithQualification(
                    jobId = job.id,
                    name = name,
                    email = email,
                    phone = phone,
                    location = loc,
                    educationLevel = edu,
                    experienceYears = exp,
                    skills = skills,
                    coverNote = cover,
                    attachments = files
                )
                isApplying = false
                when (result) {
                    is QualificationResult.Success -> {
                        successMessage = result.message
                    }
                    is QualificationResult.Ineligible -> {
                        ineligibleReason = result.reason
                    }
                    is QualificationResult.Error -> {
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    } else {
        Column(Modifier.fillMaxSize().background(BackgroundLight)) {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDark)
                }
                IconButton(onClick = {
                    if (isLoggedIn) {
                        ConnectiveRepository.toggleSaveJob(job.id)
                        val msg = if (job.isSaved) "Job unsaved" else "Job saved!"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please sign in to save jobs", Toast.LENGTH_SHORT).show()
                        onRequireAuth()
                    }
                }) {
                    Icon(
                        imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (job.isSaved) BluePrimary else TextDark
                    )
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(56.dp).background(BlueDark, RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) { Icon(Icons.Default.Code, null, tint = Color.White) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)
                                Text(job.company, color = TextGray)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = TextGray, modifier = Modifier.size(18.dp))
                            Text(" ${job.location}", color = TextGray, fontSize = 13.sp)
                            Spacer(Modifier.width(16.dp))
                            Icon(Icons.Default.School, null, tint = TextGray, modifier = Modifier.size(18.dp))
                            Text(" ${job.type}", color = TextGray, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(job.salary, fontWeight = FontWeight.Medium, color = TextDark)
                    }
                }

                Spacer(Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Minimum Qualification Criteria", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("• Minimum Education: ${job.minEducation}", fontSize = 13.sp, color = TextDark)
                        Text("• Required Experience: ${job.minExperienceYears}+ year(s)", fontSize = 13.sp, color = TextDark)
                    }
                }

                Spacer(Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("About the job", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Spacer(Modifier.height(6.dp))
                        Text(job.description, color = TextGray, fontSize = 14.sp)

                        Spacer(Modifier.height(16.dp))
                        Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Spacer(Modifier.height(6.dp))
                        job.requirements.forEach { requirement ->
                            Text("•  $requirement", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            Column(Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        if (isLoggedIn) {
                            isApplying = true
                        } else {
                            Toast.makeText(context, "Please sign in to apply", Toast.LENGTH_SHORT).show()
                            onRequireAuth()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Apply Now") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        if (isLoggedIn) {
                            ConnectiveRepository.toggleSaveJob(job.id)
                            val msg = if (job.isSaved) "Job unsaved" else "Job saved!"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please sign in to save jobs", Toast.LENGTH_SHORT).show()
                            onRequireAuth()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(10.dp)
                ) { Text(if (job.isSaved) "Remove from Saved" else "Save Job") }
            }
        }
    }

    if (ineligibleReason != null) {
        AlertDialog(
            onDismissRequest = { ineligibleReason = null },
            icon = { Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFD9534F), modifier = Modifier.size(36.dp)) },
            title = { Text("Application Ineligible", fontWeight = FontWeight.Bold) },
            text = { Text(ineligibleReason ?: "", fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { ineligibleReason = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) { Text("OK") }
            }
        )
    }

    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { successMessage = null },
            title = { Text("Application Submitted!", fontWeight = FontWeight.Bold, color = BluePrimary) },
            text = { Text(successMessage ?: "", fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { successMessage = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) { Text("Done") }
            }
        )
    }
}

@Composable
private fun JobApplicationFormScreen(
    jobTitle: String,
    company: String,
    minEducation: String,
    minExperience: Int,
    initialName: String,
    initialEmail: String,
    initialPhone: String,
    initialLocation: String,
    initialEducation: String,
    initialExperience: Int,
    onBack: () -> Unit,
    onSubmit: (
        name: String,
        email: String,
        phone: String,
        location: String,
        education: String,
        experience: Int,
        skills: String,
        cover: String,
        attachments: List<String>
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var location by remember { mutableStateOf(initialLocation) }
    var education by remember { mutableStateOf(initialEducation) }
    var experience by remember { mutableIntStateOf(initialExperience) }
    var skills by remember { mutableStateOf("") }
    var coverNote by remember { mutableStateOf("") }
    val attachedFiles = remember { mutableStateListOf("Degree_Certificate.pdf", "Passport_Photo.png") }

    val educationLevels = listOf("Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School")
    val experienceOptions = listOf(0, 1, 2, 3, 5)

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        // Top Bar with Back Button
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDark)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Job Application Form", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                Text("$jobTitle · $company", fontSize = 12.sp, color = TextGray)
            }
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Job Qualification Criteria:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Spacer(Modifier.height(4.dp))
                    Text("• Minimum Education: $minEducation", fontSize = 12.sp, color = BluePrimary)
                    Text("• Minimum Experience: $minExperience year(s)", fontSize = 12.sp, color = BluePrimary)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Personal & Contact Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Current Location / City") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("2. Qualifications & Experience", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)

                    Text("Highest Education Level:", fontSize = 13.sp, color = TextGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        educationLevels.take(3).forEach { edu ->
                            val selected = edu == education
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { education = edu }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        educationLevels.drop(3).forEach { edu ->
                            val selected = edu == education
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { education = edu }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text(edu, fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }

                    Text("Years of Relevant Experience:", fontSize = 13.sp, color = TextGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        experienceOptions.forEach { exp ->
                            val selected = exp == experience
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(16.dp))
                                    .clickable { experience = exp }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("$exp yr(s)", fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                        }
                    }

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Key Skills & Certifications") },
                        placeholder = { Text("e.g. Kotlin, REST APIs, Git") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = coverNote,
                        onValueChange = { coverNote = it },
                        label = { Text("Cover Statement / Additional Details") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D7DE)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("3. Attach Qualification Documents & Images", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Text("Attach copies of your Degree / Diploma certificate, CV, or ID photo for employer verification.", fontSize = 12.sp, color = TextGray)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val newDoc = "Qualification_Cert_${attachedFiles.size + 1}.pdf"
                                if (!attachedFiles.contains(newDoc)) attachedFiles.add(newDoc)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AttachFile, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("+ Attach Document", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val newImg = "ID_Photo_${attachedFiles.size + 1}.png"
                                if (!attachedFiles.contains(newImg)) attachedFiles.add(newImg)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("+ Attach Image", fontSize = 11.sp)
                        }
                    }

                    if (attachedFiles.isNotEmpty()) {
                        Text("Attached Files (${attachedFiles.size}):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        attachedFiles.forEach { file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BackgroundLight, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (file.endsWith(".png") || file.endsWith(".jpg")) Icons.Default.Image else Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(file, fontSize = 12.sp, color = TextDark)
                                }
                                IconButton(
                                    onClick = { attachedFiles.remove(file) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, "Remove attachment", tint = TextGray, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action Buttons: Submit Application & Cancel
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    onSubmit(name, email, phone, location, education, experience, skills, coverNote, attachedFiles)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) { Text("Submit Application") }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Cancel") }
        }
    }
}