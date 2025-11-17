package com.example.revd_up.presentation.views.customer

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

/**
 * Step 2: User applies filters or edits the image/video.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterEditScreen(
    mediaUri: Uri,
    onNext: (filterIndex: Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedFilterIndex by remember { mutableStateOf(0) }
    val filters = remember { listOf("Original", "Vivid", "Muted", "Noir", "Sepia") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter & Edit", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
                actions = {
                    TextButton(onClick = { onNext(selectedFilterIndex) }) {
                        Text("Next", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Filtered Media Preview ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, 'mediaUri' would be loaded and modified based on 'selectedFilterIndex'
                if (mediaUri.toString().contains("mock")) {
                    Text("Filtered Preview of $selectedFilterIndex", color = Color.White)
                } else {
                    // Use Coil for a real image placeholder (if URI was valid)
                    Image(
                        painter = rememberAsyncImagePainter(model = mediaUri),
                        contentDescription = "Filtered Media Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // --- Filter Selector ---
            Text(
                "Filters",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filters) { index, filterName ->
                    FilterItem(
                        name = filterName,
                        isSelected = index == selectedFilterIndex,
                        onClick = { selectedFilterIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Edit Tab Placeholder ---
            Divider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Edit Tools (Brightness, Crop, etc. would go here)", color = Color.Gray)
            }
        }
    }
}

@Composable
fun FilterItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), color = Color.Black)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.bodySmall)
    }
}