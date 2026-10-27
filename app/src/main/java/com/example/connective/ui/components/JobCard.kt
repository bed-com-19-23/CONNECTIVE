package com.example.connective.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connective.data.Job
import com.example.connective.ui.theme.*

@Composable
fun JobCard(
    job: Job,
    isSaved: Boolean = false,
    onSaveClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(48.dp).background(BluePrimary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Code, null, tint = Color.White) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(job.title, fontWeight = FontWeight.Bold, color = TextDark)
                Text(job.company, color = TextGray, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(LightBlueBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(job.category, fontSize = 10.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text("${job.location} · ${job.type}", color = TextGray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(job.salary, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
            }
            if (onSaveClick != null) {
                IconButton(
                    onClick = { onSaveClick.invoke() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save job",
                        tint = if (isSaved) BluePrimary else TextGray
                    )
                }
            }
        }
    }
}
