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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.ui.components.AuthField
import com.example.connective.ui.theme.*
import com.example.connective.viewmodel.AuthViewModel
import com.example.connective.viewmodel.UiState

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val resetState by viewModel.resetPasswordState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UiState.Success -> {
                viewModel.resetState()
                onLoginSuccess()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(resetState) {
        when (val state = resetState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_LONG).show()
                showForgotDialog = false
                viewModel.resetState()
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

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

                    AuthField("Email", email, { email = it }, "you@example.com", KeyboardType.Email)
                    AuthField("Password", password, { password = it }, "At least 8 characters", isPassword = true)

                    Text(
                        "Forgot password?",
                        color = BluePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                resetEmail = email
                                showForgotDialog = true
                            }
                    )
                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.login(email, password)
                        },
                        enabled = loginState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (loginState is UiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Log In", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Don't have an account? ", color = TextGray, fontSize = 14.sp)
                        Text(
                            "Sign up",
                            color = BluePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onRegister() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your email address and we will send you a password reset link.", fontSize = 13.sp, color = TextGray)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.sendPasswordReset(resetEmail) },
                    enabled = resetState !is UiState.Loading
                ) {
                    if (resetState is UiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Send Reset Link")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) { Text("Cancel") }
            }
        )
    }
}
