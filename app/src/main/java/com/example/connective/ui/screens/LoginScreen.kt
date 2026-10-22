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
import androidx.compose.ui.Alignment
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
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
                    Text("Welcome back", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.height(6.dp))
                    Text("Log in to continue to Connective", color = TextGray)
                    Spacer(Modifier.height(24.dp))

                    AuthField("Email", email, { email = it; emailError = null },
                        "you@example.com", KeyboardType.Email, error = emailError)
                    AuthField("Password", password, { password = it; passwordError = null },
                        "Enter your password", isPassword = true, error = passwordError)

                    Text(
                        "Forgot password?", color = BluePrimary, fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.End).clickable { }
                    )
                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            emailError = if (!email.contains("@") || !email.contains(".")) "Enter a valid email" else null
                            passwordError = if (password.length < 6) "Password must be at least 6 characters" else null
                            if (emailError == null && passwordError == null) {
                                ConnectiveRepository.loginUser(email = email)
                                onLogin()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Log In") }

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Don't have an account? ", color = TextGray, fontSize = 14.sp)
                        Text(
                            "Sign up", color = BluePrimary, fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRegister() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}