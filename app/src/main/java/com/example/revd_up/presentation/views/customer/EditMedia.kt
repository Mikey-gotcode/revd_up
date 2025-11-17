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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlin.math.roundToInt

// Data class to hold all image adjustment state
data class ImageAdjustments(
    val filterIndex: Int = 0, // 0 to 4 (Original, Vivid, Muted, Noir, Sepia)
    val brightness: Float = 0f // -1.0 (Dark) to 1.0 (Bright)
)

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
    // Consolidated state for all adjustments
    var adjustments by remember { mutableStateOf(ImageAdjustments()) }

    // UI State for Tabs
    val tabs = remember { listOf("FILTERS", "ADJUST") }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val filters = remember { listOf("Original", "Vivid", "Muted", "Noir", "Sepia") }

    // Calculate the combined color matrix based on current adjustments
    val combinedColorMatrix = remember(adjustments) {
        applyFilterAndEdits(adjustments)
    }

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
                    TextButton(onClick = { onNext(adjustments.filterIndex) }) {
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
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                // Apply the combined ColorMatrix to the Image
                Image(
                    painter = rememberAsyncImagePainter(model = mediaUri),
                    contentDescription = "Filtered Media Preview",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(combinedColorMatrix),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // --- Tab Selector (Filters / Adjust) ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // --- Content Based on Tab Selection ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> FilterContent(
                        filters = filters,
                        adjustments = adjustments,
                        onFilterSelected = { index ->
                            adjustments = adjustments.copy(filterIndex = index)
                        }
                    )
                    1 -> EditContent(
                        adjustments = adjustments,
                        onBrightnessChange = { value ->
                            adjustments = adjustments.copy(brightness = value)
                        }
                    )
                }
            }
        }
    }
}

// --- Filter Tab Content ---

@Composable
fun FilterContent(
    filters: List<String>,
    adjustments: ImageAdjustments,
    onFilterSelected: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(filters) { index, filterName ->
            val isSelected = index == adjustments.filterIndex
            FilterItem(
                name = filterName,
                // We use a mock image URI here to display the filter effect on the thumbnail
                mockUri = Uri.parse("https://placehold.co/100x100/4CAF50/FFFFFF?text=${filterName.take(1)}"),
                isSelected = isSelected,
                colorMatrix = applyFilterAndEdits(ImageAdjustments(filterIndex = index)),
                onClick = { onFilterSelected(index) }
            )
        }
    }
}

@Composable
fun FilterItem(
    name: String,
    mockUri: Uri,
    isSelected: Boolean,
    colorMatrix: ColorMatrix,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // Thumbnail with the applied filter effect
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = mockUri),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.bodySmall)
    }
}

// --- Edit Tab Content ---

@Composable
fun EditContent(
    adjustments: ImageAdjustments,
    onBrightnessChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Brightness Slider
        Text(
            "Brightness: ${(adjustments.brightness * 100).roundToInt()}%",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Slider(
            value = adjustments.brightness,
            onValueChange = onBrightnessChange,
            valueRange = -1f..1f, // Range from dark (-1) to bright (1)
            steps = 98 // Provides fine control
        )
        Text(
            "Controls like Contrast, Saturation, and Crop would be added here.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}


/**
 * Core image processing logic. Creates a ColorMatrix combining the selected filter and manual edits.
 * @param adjustments The current state of image modifications.
 * @return A ColorMatrix ready to be applied as a ColorFilter.
 */
fun applyFilterAndEdits(adjustments: ImageAdjustments): ColorMatrix {
    val matrix = ColorMatrix()

    // 1. Apply Filter Preset
    when (adjustments.filterIndex) {
        1 -> matrix.setToSaturation(1.5f) // Vivid (High Saturation)
        2 -> matrix.setToSaturation(0.2f) // Muted (Low Saturation)
        3 -> matrix.setToSaturation(0f)   // Noir (Grayscale/B&W)
        4 -> { // Sepia
            val sepiaMatrix = ColorMatrix(
                floatArrayOf(
                    0.393f, 0.769f, 0.189f, 0f, 0f,
                    0.349f, 0.686f, 0.168f, 0f, 0f,
                    0.272f, 0.534f, 0.131f, 0f, 0f,
                    0f,     0f,     0f,     1f, 0f
                )
            )
            matrix.set(sepiaMatrix)
        }
        // Case 0 (Original) is the default, where the matrix remains an identity matrix.
    }

    // 2. Apply Manual Edits (Brightness)
    // This is applied on top of the selected filter.
    val brightnessOffset = adjustments.brightness // -1f to 1f, used as an offset

    // **THE FIX IS HERE:** Create the brightness matrix and multiply it with the main matrix.
    // Use the *= operator which is the Compose equivalent of postConcat.
    val brightnessMatrix = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, brightnessOffset,
            0f, 1f, 0f, 0f, brightnessOffset,
            0f, 0f, 1f, 0f, brightnessOffset,
            0f, 0f, 0f, 1f, 0f
        )
    )
    matrix *= brightnessMatrix

    // 3. Return the final, combined matrix
    return matrix
}
