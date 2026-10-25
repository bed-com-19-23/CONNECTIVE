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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.theme.*

@Composable
fun ProfileScreen(
    isLoggedIn: Boolean = false,
    onSignInClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val user = ConnectiveRepository.currentUser

    var isEditing by remember { mutableStateOf(false) }
    var activeDialog by remember { mutableStateOf<String?>(null) } // "personal", "education", "work", "preferences"
    var activeStatDialog by remember { mutableStateOf<String?>(null) } // "Applied", "Shortlisted", "Hired", "Saved"

    var editTitle by remember { mutableStateOf(user?.title ?: "") }
    var editLocation by remember { mutableStateOf(user?.location ?: "") }
    var editBio by remember { mutableStateOf(user?.bio ?: "") }

    LaunchedEffect(user) {
        if (user != null) {
            editTitle = user.title
            editLocation = user.location
            editBio = user.bio
        }
    }

    if (!isLoggedIn || user == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier
                    .size(90.dp)
                    .background(LightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = BluePrimary, modifier = Modifier.size(48.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Guest Account", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextNavy)
            Spacer(Modifier.height(8.dp))
            Text(
                "Sign in or create an account to view your profile, manage applications, and unlock all features.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Sign In")
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Account")
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(HeroBannerGradient)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .offset(y = (-45).dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(90.dp)
                        .background(BluePrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.avatarInitials,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)
                Text(
                    if (user.role == "employer") user.companyName.ifBlank { "Employer" } else user.title,
                    color = BluePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(user.location, color = TextGray, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    user.bio,
                    color = TextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))

                if (!isEditing) {
                    OutlinedButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text("Edit Profile") }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Edit Quick Info", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editTitle,
                                onValueChange = { editTitle = it },
                                label = { Text("Title / Position") },
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
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editLocation,
                                onValueChange = { editLocation = it },
                                label = { Text("Location") },
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
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editBio,
                                onValueChange = { editBio = it },
                                label = { Text("Bio") },
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
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                                Button(onClick = {
                                    ConnectiveRepository.updateUserProfileDetails(
                                        name = user.name,
                                        phone = user.phone,
                                        location = editLocation,
                                        title = editTitle,
                                        educationLevel = user.educationLevel,
                                        experienceYears = user.experienceYears,
                                        skills = user.skills,
                                        bio = editBio
                                    )
                                    isEditing = false
                                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                                }) { Text("Save") }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                val userApps = ConnectiveRepository.applications.filter { app ->
                    user.role == "employer" || app.candidateEmail.equals(user.email, ignoreCase = true)
                }
                val appliedCount = userApps.count { it.status.equals("Applied", ignoreCase = true) }
                val shortlistedCount = userApps.count { it.status.equals("Shortlisted", ignoreCase = true) }
                val hiredCount = userApps.count { it.status.equals("Hired", ignoreCase = true) }
                val savedCount = ConnectiveRepository.jobs.count { it.isSaved }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Stat(appliedCount.toString(), "Applied") { activeStatDialog = "Applied" }
                        Stat(shortlistedCount.toString(), "Shortlisted") { activeStatDialog = "Shortlisted" }
                        Stat(hiredCount.toString(), "Hired") { activeStatDialog = "Hired" }
                        Stat(savedCount.toString(), "Saved") { activeStatDialog = "Saved" }
                    }
                }

                Spacer(Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        MenuRow(Icons.Default.Person, "Personal Information") {
                            activeDialog = "personal"
                        }
                        HorizontalDivider(color = LightBlueBg)
                        MenuRow(Icons.Default.School, "Education & Skills") {
                            activeDialog = "education"
                        }
                        HorizontalDivider(color = LightBlueBg)
                        MenuRow(Icons.Default.Work, "Work Experience") {
                            activeDialog = "work"
                        }
                        HorizontalDivider(color = LightBlueBg)
                        MenuRow(Icons.Default.Settings, "Preferences") {
                            activeDialog = "preferences"
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        ConnectiveRepository.logout()
                        onSignOutClick()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }
            }
        }

        // Dialog for Stat items (Applied, Shortlisted, Hired, Saved)
        if (activeStatDialog != null) {
            val dialogCategory = activeStatDialog!!
            val userApps = ConnectiveRepository.applications.filter { app ->
                user.role == "employer" || app.candidateEmail.equals(user.email, ignoreCase = true)
            }
            val savedJobsCount = ConnectiveRepository.jobs.count { it.isSaved }

            AlertDialog(
                onDismissRequest = { activeStatDialog = null },
                title = {
                    Text(
                        text = when (dialogCategory) {
                            "Saved" -> "Saved Jobs ($savedJobsCount)"
                            else -> "$dialogCategory Applications"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextNavy
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (dialogCategory == "Saved") {
                            val savedJobs = ConnectiveRepository.jobs.filter { it.isSaved }
                            if (savedJobs.isEmpty()) {
                                Text("No saved jobs yet.", color = TextGray, fontSize = 13.sp)
                            } else {
                                savedJobs.forEach { job ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = BackgroundLight)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                                Text(job.company, fontSize = 12.sp, color = TextGray)
                                                Text(job.location, fontSize = 11.sp, color = BluePrimary)
                                            }
                                            IconButton(onClick = {
                                                ConnectiveRepository.toggleSaveJob(job.id)
                                            }) {
                                                Icon(
                                                    Icons.Default.Bookmark,
                                                    contentDescription = "Unsave",
                                                    tint = BluePrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val filteredApps = userApps.filter {
                                it.status.equals(dialogCategory, ignoreCase = true)
                            }

                            if (filteredApps.isEmpty()) {
                                Text("No $dialogCategory applications found.", color = TextGray, fontSize = 13.sp)
                            } else {
                                filteredApps.forEach { app ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = BackgroundLight)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(app.jobTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                                                val statusColor = when (app.status) {
                                                    "Shortlisted" -> Color(0xFF2E9E5B)
                                                    "Hired" -> Color(0xFF7B3FE4)
                                                    "Rejected" -> Color(0xFFD9534F)
                                                    else -> BluePrimary
                                                }
                                                Text(
                                                    app.status,
                                                    color = statusColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(Modifier.height(2.dp))
                                            Text(app.company, fontSize = 12.sp, color = TextGray)
                                            Text("Applied: ${app.appliedDate}", fontSize = 11.sp, color = TextGray)
                                            if (app.attachments.isNotEmpty()) {
                                                Spacer(Modifier.height(4.dp))
                                                Text("Attachments: ${app.attachments.joinToString(", ")}", fontSize = 10.sp, color = BluePrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { activeStatDialog = null }) {
                        Text("Close")
                    }
                }
            )
        }

        // Dialogs for Menu Items
        when (activeDialog) {
            "personal" -> {
                var pName by remember { mutableStateOf(user.name) }
                var pPhone by remember { mutableStateOf(user.phone) }
                var pEmail by remember { mutableStateOf(user.email) }
                var pLocation by remember { mutableStateOf(user.location) }

                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    title = { Text("Personal Information", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pName,
                                onValueChange = { pName = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = pPhone,
                                onValueChange = { pPhone = it },
                                label = { Text("Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = pEmail,
                                onValueChange = { pEmail = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = pLocation,
                                onValueChange = { pLocation = it },
                                label = { Text("Location") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            ConnectiveRepository.updateUserProfileDetails(
                                name = pName,
                                email = pEmail,
                                phone = pPhone,
                                location = pLocation,
                                title = user.title,
                                educationLevel = user.educationLevel,
                                experienceYears = user.experienceYears,
                                skills = user.skills,
                                bio = user.bio
                            )
                            activeDialog = null
                            Toast.makeText(context, "Personal Information saved!", Toast.LENGTH_SHORT).show()
                        }) { Text("Save Changes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("Cancel") }
                    }
                )
            }

            "education" -> {
                var eduLevel by remember { mutableStateOf(user.educationLevel) }
                var userSkills by remember { mutableStateOf(user.skills) }
                val educationLevels = listOf("Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School")

                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    title = { Text("Education & Skills", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Highest Education Level:", fontSize = 12.sp, color = TextGray)
                            educationLevels.forEach { lvl ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { eduLevel = lvl }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(selected = lvl == eduLevel, onClick = { eduLevel = lvl })
                                    Spacer(Modifier.width(6.dp))
                                    Text(lvl, fontSize = 13.sp, color = TextDark)
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = userSkills,
                                onValueChange = { userSkills = it },
                                label = { Text("Key Skills & Certifications") },
                                placeholder = { Text("e.g. Kotlin, REST APIs, Git") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            ConnectiveRepository.updateUserProfileDetails(
                                name = user.name,
                                phone = user.phone,
                                location = user.location,
                                title = user.title,
                                educationLevel = eduLevel,
                                experienceYears = user.experienceYears,
                                skills = userSkills,
                                bio = user.bio
                            )
                            activeDialog = null
                            Toast.makeText(context, "Education & Skills saved!", Toast.LENGTH_SHORT).show()
                        }) { Text("Save Changes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("Cancel") }
                    }
                )
            }

            "work" -> {
                var workTitle by remember { mutableStateOf(user.title) }
                var expYears by remember { mutableIntStateOf(user.experienceYears) }
                var userBio by remember { mutableStateOf(user.bio) }
                val expOptions = listOf(0, 1, 2, 3, 5)

                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    title = { Text("Work Experience", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = workTitle,
                                onValueChange = { workTitle = it },
                                label = { Text("Current or Preferred Job Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text("Years of Experience:", fontSize = 12.sp, color = TextGray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                expOptions.forEach { yrs ->
                                    val selected = yrs == expYears
                                    Box(
                                        Modifier
                                            .background(if (selected) BluePrimary else LightBlueBg, RoundedCornerShape(16.dp))
                                            .clickable { expYears = yrs }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) { Text("$yrs yr(s)", fontSize = 11.sp, color = if (selected) Color.White else BluePrimary) }
                                }
                            }

                            OutlinedTextField(
                                value = userBio,
                                onValueChange = { userBio = it },
                                label = { Text("Career Summary / Bio") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            ConnectiveRepository.updateUserProfileDetails(
                                name = user.name,
                                phone = user.phone,
                                location = user.location,
                                title = workTitle,
                                educationLevel = user.educationLevel,
                                experienceYears = expYears,
                                skills = user.skills,
                                bio = userBio
                            )
                            activeDialog = null
                            Toast.makeText(context, "Work Experience saved!", Toast.LENGTH_SHORT).show()
                        }) { Text("Save Changes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("Cancel") }
                    }
                )
            }

            "preferences" -> {
                var jobAlerts by remember { mutableStateOf(true) }
                var qualificationMatch by remember { mutableStateOf(true) }
                var remoteAllowed by remember { mutableStateOf(true) }
                var publicProfile by remember { mutableStateOf(true) }

                AlertDialog(
                    onDismissRequest = { activeDialog = null },
                    title = { Text("Job Preferences", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("New Job Alerts & Notifications", fontSize = 13.sp, color = TextDark, modifier = Modifier.weight(1f))
                                Switch(checked = jobAlerts, onCheckedChange = { jobAlerts = it })
                            }
                            HorizontalDivider(color = LightBlueBg)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Filter Jobs by My Qualification", fontSize = 13.sp, color = TextDark, modifier = Modifier.weight(1f))
                                Switch(checked = qualificationMatch, onCheckedChange = { qualificationMatch = it })
                            }
                            HorizontalDivider(color = LightBlueBg)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Available for Remote Work", fontSize = 13.sp, color = TextDark, modifier = Modifier.weight(1f))
                                Switch(checked = remoteAllowed, onCheckedChange = { remoteAllowed = it })
                            }
                            HorizontalDivider(color = LightBlueBg)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Visible Profile for Employers", fontSize = 13.sp, color = TextDark, modifier = Modifier.weight(1f))
                                Switch(checked = publicProfile, onCheckedChange = { publicProfile = it })
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            activeDialog = null
                            Toast.makeText(context, "Preferences saved!", Toast.LENGTH_SHORT).show()
                        }) { Text("Save Preferences") }
                    },
                    dismissButton = {
                        TextButton(onClick = { activeDialog = null }) { Text("Cancel") }
                    }
                )
            }
        }
    }
}

@Composable
private fun Stat(number: String, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(number, color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = TextGray, fontSize = 12.sp)
    }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BluePrimary)
        Spacer(Modifier.width(14.dp))
        Text(title, color = TextDark, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = TextGray)
    }
}
