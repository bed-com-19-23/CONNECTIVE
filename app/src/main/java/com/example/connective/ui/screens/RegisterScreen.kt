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
fun RegisterScreen(
    onRegistered: (String) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current

    var role by remember { mutableStateOf("seeker") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val registerState by viewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is UiState.Success -> {
                viewModel.resetState()
                onRegistered(state.data.role)
            }
            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
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
                        label = if (role == "employer") "Company Name" else "Full Name",
                        value = name,
                        onChange = { name = it },
                        hint = if (role == "employer") "e.g. Sunrise Media" else "e.g. Shepherd Sadick"
                    )
                    AuthField(
                        label = "Email",
                        value = email,
                        onChange = { email = it },
                        hint = "you@example.com",
                        keyboardType = KeyboardType.Email
                    )
                    AuthField(
                        label = "Password",
                        value = password,
                        onChange = { password = it },
                        hint = "At least 8 characters",
                        isPassword = true
                    )
                    AuthField(
                        label = "Confirm Password",
                        value = confirm,
                        onChange = { confirm = it },
                        hint = "Repeat your password",
                        isPassword = true
                    )

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.register(
                                name = name,
                                email = email,
                                pass = password,
                                confirmPass = confirm,
                                role = role,
                                companyName = if (role == "employer") name else ""
                            )
                        },
                        enabled = registerState !is UiState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (registerState is UiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                        Text(
                            "Log in",
                            color = BluePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onLogin() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
