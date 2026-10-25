package com.example.connective.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.ui.theme.*

@Composable
fun AuthField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    error: String? = null
) {
    var visible by remember { mutableStateOf(false) }

    Text(label, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(hint, fontSize = 13.sp, color = TextGray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color(0xFFD0D7DE)
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error != null,
        supportingText = if (error != null) { { Text(error) } } else null,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation()
        else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Show or hide password"
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(10.dp)
    )
    Spacer(Modifier.height(12.dp))
}
