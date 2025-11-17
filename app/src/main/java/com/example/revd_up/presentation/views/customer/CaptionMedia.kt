package com.example.revd_up.presentation.views.customer

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.revd_up.data.api.RevdUpPostService
import kotlinx.coroutines.launch

/**
 * Step 3: User fills in caption and final details before posting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptionDetailsScreen(
    mediaUri: Uri,
    filterIndex: Int,
    postService: RevdUpPostService,
    onPostSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var captionText by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    val handlePost = {
        if (captionText.isBlank()) {
            Toast.makeText(context, "Please add a caption.", Toast.LENGTH_SHORT).show()
        } else {
            isPosting = true
            scope.launch {
                Log.d("CaptionDetailsScreen", "Attempting to post with filter index: $filterIndex")

                // Mock user ID and tags for the API call
                val mockUserId = "user-${System.currentTimeMillis()}"

                val result = postService.createPost(
                    caption = "$captionText (Applied Filter: $filterIndex)",
                    mediaUrl = mediaUri.toString(), // The URI of the picked media
                    tags = listOf("revdup", "car", location).filter { it.isNotBlank() }
                )

                if (result != null) {
                    Toast.makeText(context, "Post successful!", Toast.LENGTH_SHORT).show()
                    onPostSuccess()
                } else {
                    Toast.makeText(context, "Post failed. Try again.", Toast.LENGTH_LONG).show()
                    isPosting = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isPosting) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    if (isPosting) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                    } else {
                        TextButton(onClick = handlePost as () -> Unit, enabled = captionText.isNotBlank()) {
                            Text("Share", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- Preview Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color.LightGray)
                ) {
                    // Display the selected image with a visual indicator of the filter
                    Image(
                        // Coerce the Uri to String for Coil, and use a placeholder if needed
                        painter = rememberAsyncImagePainter(model = mediaUri),
                        contentDescription = "Post Preview with Filter $filterIndex",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        "F$filterIndex",
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White.copy(alpha = 0.7f))
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Caption Input
                OutlinedTextField(
                    value = captionText,
                    onValueChange = { captionText = it },
                    label = { Text("Write a caption...") },
                    modifier = Modifier.weight(1f),
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isPosting
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Additional Details ---
            Divider()

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add location (e.g., Garage Name, City)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !isPosting
            )

            // Optional: Tag People, Advanced Settings
            TextButton(onClick = { /* Navigate to Tagging */ }, enabled = !isPosting) {
                Text("Tag People")
            }

            TextButton(onClick = { /* Navigate to Settings */ }, enabled = !isPosting) {
                Text("Advanced Settings")
            }
        }
    }
}