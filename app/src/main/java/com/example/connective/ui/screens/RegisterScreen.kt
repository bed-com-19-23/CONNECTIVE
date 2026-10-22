package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.components.AuthField
import com.example.connective.ui.theme.*

@Composable
fun RegisterScreen(onRegistered: (String) -> Unit, onLogin: () -> Unit, onBack: () -> Unit) {
    var role by remember { mutableStateOf("seeker") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(BackgroundLight)) {
        IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDark)
        }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Create account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(6.dp))
                    Text("Join Connective in a few steps", color = TextGray)
                    Spacer(Modifier.height(20.dp))

                    Text("I am a", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("seeker" to "Job Seeker", "employer" to "Employer").forEach { (value, label) ->
                            val selected = role == value
                            Box(
                                Modifier
                                    .background(if (selected) BluePrimary else Color(0xFFE3EAFB), RoundedCornerShape(20.dp))
                                    .clickable { role = value }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(label, color = if (selected) Color.White else BluePrimary, fontSize = 14.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    AuthField(
                        if (role == "employer") "Company Name" else "Full Name",
                        name, { name = it; nameError = null },
                        if (role == "employer") "e.g. Sunrise Media" else "e.g. Shepherd Sadick",
                        error = nameError
                    )
                    AuthField("Email", email, { email = it; emailError = null },
                        "you@example.com", KeyboardType.Email, error = emailError)
                    AuthField("Password", password, { password = it; passwordError = null },
                        "At least 6 characters", isPassword = true, error = passwordError)
                    AuthField("Confirm Password", confirm, { confirm = it; confirmError = null },
                        "Repeat your password", isPassword = true, error = confirmError)

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            nameError = if (name.isBlank()) "This field is required" else null
                            emailError = if (!email.contains("@") || !email.contains(".")) "Enter a valid email" else null
                            passwordError = if (password.length < 6) "Password must be at least 6 characters" else null
                            confirmError = if (confirm != password) "Passwords do not match" else null
                            if (listOf(nameError, emailError, passwordError, confirmError).all { it == null }) {
                                ConnectiveRepository.registerUser(
                                    name = name,
                                    email = email,
                                    role = role,
                                    companyName = if (role == "employer") name else ""
                                )
                                onRegistered(role)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Create Account") }

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                        Text(
                            "Log in", color = BluePrimary, fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLogin() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}