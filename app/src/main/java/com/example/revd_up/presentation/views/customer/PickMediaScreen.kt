package com.example.revd_up.presentation.views.customer

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Step 1: User picks media (image/video) from their device or takes a new one.
 * The UI is based on the screenshot provided (showing a camera roll and a capture button).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickMediaScreen(
    onMediaPicked: (Uri) -> Unit,
    onBack: () -> Unit
) {
    // Mock state for simulation (in a real app, this would be managed by an Activity Result Contract)
    val mockUri = remember { Uri.parse("content://mock/image/12345") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    TextButton(onClick = { onMediaPicked(mockUri) }) {
                        Text("Next", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Mock Image Preview Area (based on screenshot) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Square aspect ratio like Instagram
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Selected Media Preview",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // --- Gallery/Camera Controls ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mock Gallery Button
                Button(onClick = { onMediaPicked(mockUri) }) {
                    Text("Gallery")
                }

                // Capture Button (based on screenshot)
                IconButton(
                    onClick = { onMediaPicked(mockUri) }, // Mock: Capture immediately goes to next step
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Mock Media Type Toggle
                TextButton(onClick = { /* Toggle media type */ }) {
                    Text("Video")
                }
            }

            Divider()

            // --- Mock Recent Media Grid (based on screenshot) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("Mock Media Grid / Camera Roll", color = Color.Gray)
            }
        }
    }
}