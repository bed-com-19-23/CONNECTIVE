package com.example.connective.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.connective.data.Job
import com.example.connective.data.User
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.JobDetailsViewModel
import com.example.connective.viewmodel.UiState

@Composable
fun JobDetailsScreen(
    jobId: String,
    currentUser: User?,
    savedJobIds: Set<String> = emptySet(),
    onToggleSave: (String, Boolean) -> Unit = { _, _ -> },
    onRequireAuth: () -> Unit = {},
    onBack: () -> Unit,
    viewModel: JobDetailsViewModel
) {
    val context = LocalContext.current
    val jobState by viewModel.jobState.collectAsState()
    val applicationResult by viewModel.applicationResult.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()

    var isApplying by remember { mutableStateOf(false) }
    var ineligibleReason by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(jobId) {
        viewModel.loadJob(jobId)
    }

    LaunchedEffect(applicationResult) {
        when (val result = applicationResult) {
            is UiState.Success -> {
                isApplying = false
                successMessage = result.data
                viewModel.resetApplicationState()
            }
            is UiState.Error -> {
                ineligibleReason = result.message
                viewModel.resetApplicationState()
            }
            else -> {}
        }
    }

    when (val state = jobState) {
        is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = TextGray, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onBack) { Text("Go Back") }
                }
            }
        }
        is UiState.Success -> {
            val job = state.data
            val isSaved = savedJobIds.contains(job.id)

            if (isApplying && currentUser != null) {
                JobApplicationFormScreen(
                    job = job,
                    seeker = currentUser,
                    uploadProgress = uploadProgress,
                    isSubmitting = applicationResult is UiState.Loading,
                    onBack = { isApplying = false },
                    onSubmit = { phone, loc, edu, exp, skills, cover, files ->
                        viewModel.submitApplication(
                            job = job,
                            seeker = currentUser,
                            phone = phone,
                            location = loc,
                            educationLevel = edu,
                            experienceYears = exp,
                            skills = skills,
                            coverNote = cover,
                            pickedFiles = files
                        )
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
                            if (currentUser != null) {
                                onToggleSave(job.id, isSaved)
                            } else {
                                Toast.makeText(context, "Please sign in to save jobs", Toast.LENGTH_SHORT).show()
                                onRequireAuth()
                            }
                        }) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) BluePrimary else TextDark
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
                                Text("Category: ${job.category}", fontSize = 12.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
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

                                if (job.requirements.isNotEmpty()) {
                                    Spacer(Modifier.height(16.dp))
                                    Text("Requirements", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                                    Spacer(Modifier.height(6.dp))
                                    job.requirements.forEach { requirement ->
                                        Text("•  $requirement", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Column(Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                if (currentUser != null) {
                                    if (currentUser.role == "seeker") {
                                        isApplying = true
                                    } else {
                                        Toast.makeText(context, "Employers / Admins cannot apply for jobs", Toast.LENGTH_SHORT).show()
                                    }
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
                                if (currentUser != null) {
                                    onToggleSave(job.id, isSaved)
                                } else {
                                    Toast.makeText(context, "Please sign in to save jobs", Toast.LENGTH_SHORT).show()
                                    onRequireAuth()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text(if (isSaved) "Remove from Saved" else "Save Job") }
                    }
                }
            }
        }
        else -> {}
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
    job: Job,
    seeker: User,
    uploadProgress: Float,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmit: (
        phone: String,
        location: String,
        education: String,
        experience: Int,
        skills: String,
        cover: String,
        pickedFiles: List<Pair<Uri, String>>
    ) -> Unit
) {
    val context = LocalContext.current

    // Pre-fill fields from candidate profile
    var phone by remember { mutableStateOf(seeker.phone) }
    var location by remember { mutableStateOf(seeker.location) }
    var education by remember { mutableStateOf(seeker.educationLevel) }
    var experience by remember { mutableIntStateOf(seeker.experienceYears) }
    var skills by remember { mutableStateOf(seeker.skills) }
    var coverNote by remember { mutableStateOf("") }

    val pickedFiles = remember { mutableStateListOf<Pair<Uri, String>>() }

    // Real OpenDocument File Picker (PDF/Image <= 5MB)
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri) ?: "Document_${System.currentTimeMillis()}.pdf"
            val fileSize = getFileSizeFromUri(context, uri)

            if (fileSize > 5 * 1024 * 1024) { // Max 5 MB
                Toast.makeText(context, "File size exceeds 5 MB limit ($fileName)", Toast.LENGTH_LONG).show()
            } else {
                pickedFiles.add(Pair(uri, fileName))
                Toast.makeText(context, "Selected: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val educationLevels = listOf("Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School")
    val experienceOptions = listOf(0, 1, 2, 3, 5)

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
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
                Text("${job.title} · ${job.company}", fontSize = 12.sp, color = TextGray)
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
                    Text("• Minimum Education: ${job.minEducation}", fontSize = 12.sp, color = BluePrimary)
                    Text("• Minimum Experience: ${job.minExperienceYears} year(s)", fontSize = 12.sp, color = BluePrimary)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Pre-filled Candidate Info", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)

                    OutlinedTextField(
                        value = seeker.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Full Name (from profile)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = seeker.email,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Email Address (from profile)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
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
                        label = { Text("Location / City") },
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
                    Text("3. Attach Documents & Certificates (PDF/Image <= 5MB)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)

                    OutlinedButton(
                        onClick = {
                            documentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachFile, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("+ Choose Document or Image", fontSize = 13.sp)
                    }

                    if (pickedFiles.isNotEmpty()) {
                        Text("Selected Files (${pickedFiles.size}):", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        pickedFiles.forEach { pair ->
                            val fileName = pair.second
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
                                        imageVector = if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) Icons.Default.Image else Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(fileName, fontSize = 12.sp, color = TextDark)
                                }
                                IconButton(
                                    onClick = { pickedFiles.remove(pair) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, "Remove", tint = TextGray, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    if (isSubmitting) {
                        Spacer(Modifier.height(8.dp))
                        Text("Uploading attachments... ${(uploadProgress * 100).toInt()}%", fontSize = 12.sp, color = BluePrimary)
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    onSubmit(phone, location, education, experience, skills, coverNote, pickedFiles.toList())
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Application")
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Cancel") }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use { c ->
        if (c.moveToFirst()) {
            val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = c.getString(nameIndex)
            }
        }
    }
    return name
}

private fun getFileSizeFromUri(context: Context, uri: Uri): Long {
    var size: Long = 0
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use { c ->
        if (c.moveToFirst()) {
            val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                size = c.getLong(sizeIndex)
            }
        }
    }
    return size
}
