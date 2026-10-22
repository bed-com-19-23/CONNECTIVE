package com.example.connective.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.ui.theme.*

@Composable
fun RoleSelectScreen(onSeeker: () -> Unit, onEmployer: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(BackgroundLight).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("How will you use Connective?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(6.dp))
        Text("Choose one to continue", color = TextGray)
        Spacer(Modifier.height(28.dp))
        RoleCard(Icons.Default.Search, "I'm a Job Seeker", "Find jobs and apply easily", onSeeker)
        Spacer(Modifier.height(16.dp))
        RoleCard(Icons.Default.Business, "I'm an Employer", "Post jobs and hire top talent", onEmployer)
    }
}

@Composable
private fun RoleCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(52.dp).background(BluePrimary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Color.White) }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextDark)
                Text(subtitle, color = TextGray, fontSize = 13.sp)
            }
        }
    }
}