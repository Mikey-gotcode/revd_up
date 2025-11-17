package com.example.revd_up.presentation.views.customer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES // Correct import is present
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check // Added missing import
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "PickMediaScreen"

/**
 * Step 1: User picks media (image/video) from their device or takes a new one.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickMediaScreen(
    onMediaPicked: (Uri) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // --- State Management ---
    // The currently selected media URI for the large preview
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    // Mock list for recent media items
    val recentMediaUris = remember { mockRecentMediaUris() }

    // Set the first mock item as the initial selection
    LaunchedEffect(Unit) {
        if (selectedMediaUri == null && recentMediaUris.isNotEmpty()) {
            selectedMediaUri = recentMediaUris.first()
        }
    }

    // --- Camera Setup: Temporary URI for camera output ---
    var tempCameraFileUri by remember { mutableStateOf<Uri?>(null) }

    // Function to create a temporary file URI for the camera output
    val getTempCameraFileUri: () -> Uri = {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
        tempCameraFileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        tempCameraFileUri!!
    }

    // --- Activity Result Launchers ---

    // 1. Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempCameraFileUri != null) {
                selectedMediaUri = tempCameraFileUri
                Log.d(TAG, "Image captured and set: $selectedMediaUri")
            } else {
                Log.d(TAG, "Image capture failed or cancelled.")
            }
        }
    )

    // 2. Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedMediaUri = uri
                Log.d(TAG, "Gallery image selected: $selectedMediaUri")
            }
        }
    )

    // 3. Permission Launcher
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.CAMERA] == true -> {
                // Permission granted, launch camera
                cameraLauncher.launch(getTempCameraFileUri())
            }
            // Check for READ_MEDIA_IMAGES (for Android 13+) or READ_EXTERNAL_STORAGE (legacy)
            (permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) -> {
                // Permission granted, launch gallery
                galleryLauncher.launch("image/*")
            }
            else -> {
                permissionDeniedMessage = "Camera or Storage permissions were denied. Cannot pick media."
                Log.w(TAG, permissionDeniedMessage!!)
            }
        }
    }

    // --- Helper functions to check permissions ---
    val checkAndLaunchCamera = {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(getTempCameraFileUri())
            }
            else -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
            }
        }
    }

    val checkAndLaunchGallery = {
        // Use the appropriate permission depending on the Android version
        val permission = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) { // <-- FIX: Corrected the version check logic
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> {
                galleryLauncher.launch("image/*")
            }
            else -> {
                permissionLauncher.launch(arrayOf(permission))
            }
        }
    }

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
                    TextButton(
                        onClick = {
                            selectedMediaUri?.let(onMediaPicked)
                        },
                        enabled = selectedMediaUri != null
                    ) {
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

            // --- Selected Media Preview Area ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (selectedMediaUri != null) {
                    // Use Coil to display the selected media from URI
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedMediaUri),
                        contentDescription = "Selected Media Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        "No Media Selected",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // --- Permission Denied Message ---
            if (permissionDeniedMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text(permissionDeniedMessage!!, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall)
                }
            }


            // --- Gallery/Camera Controls ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Button
                Button(onClick = checkAndLaunchGallery) {
                    Text("Gallery")
                }

                // Capture Button
                IconButton(
                    onClick = checkAndLaunchCamera,
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

                // Media Type Toggle Placeholder
                TextButton(onClick = { /* Implement video capture/selection later */ }) {
                    Text("Video")
                }
            }

            Divider()

            // --- Recent Media Grid (Camera Roll Mock) ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(recentMediaUris) { uri ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedMediaUri = uri }
                            .clip(MaterialTheme.shapes.small)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = "Recent Media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (uri == selectedMediaUri) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * Helper function to create mock URIs for the grid display.
 * In a real application, you would query the MediaStore here.
 */
private fun mockRecentMediaUris(): List<Uri> {
    // We use external placeholder images to simulate the look of a media grid.
    return listOf(
        "https://placehold.co/100x100/A0B2C5/FFFFFF?text=Car+1",
        "https://placehold.co/100x100/C5B2A0/FFFFFF?text=Mod+2",
        "https://placehold.co/100x100/B2A0C5/FFFFFF?text=Tire+3",
        "https://placehold.co/100x100/A0C5B2/FFFFFF?text=Engine+4",
        "https://placehold.co/100x100/C5A0B2/FFFFFF?text=Wash+5",
        "https://placehold.co/100x100/B2C5A0/FFFFFF?text=Interior+6",
        "https://placehold.co/100x100/A0B2C5/FFFFFF?text=Detail+7",
        "https://placehold.co/100x100/C5B2A0/FFFFFF?text=Ride+8",
        "https://placehold.co/100x100/B2A0C5/FFFFFF?text=Tuning+9",
        "https://placehold.co/100x100/A0C5B2/FFFFFF?text=Garage+10",
        "https://placehold.co/100x100/C5A0B2/FFFFFF?text=Road+11",
        "https://placehold.co/100x100/B2C5A0/FFFFFF?text=Tool+12",
    ).map { Uri.parse(it) }
}
