package com.example.revd_up.presentation.views.mechanic

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.revd_up.data.store.AuthDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedMechanicDashboard(
    dataStore: AuthDataStore,
    onLogout: () -> Unit,
    onVerifyParts: () -> Unit = {},
    onViewVerificationHistory: () -> Unit = {},
    onManageInventory: () -> Unit = {},
    onViewJobs: () -> Unit = {},
    onDiagnosticTools: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    // ✅ Removed unused context variable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mechanic Portal") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            Log.d("MechanicDashboard", "Mechanic logout")
                            dataStore.clearAuthToken()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log Out")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- MECHANIC STATS ----------------
            Text("My Dashboard", style = MaterialTheme.typography.titleLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MechanicStatCard(title = "Pending Verifications", value = "5")
                MechanicStatCard(title = "Completed Jobs", value = "23")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MechanicStatCard(title = "Active Listings", value = "8")
                MechanicStatCard(title = "Rating", value = "4.8")
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- MECHANIC ACTIONS ----------------
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge)

            MechanicActionCard(
                title = "Verify Car Parts",
                description = "Authenticate and provide verification codes",
                icon = Icons.Default.Verified,
                onClick = onVerifyParts
            )

            MechanicActionCard(
                title = "Verification History",
                description = "View your verification records",
                icon = Icons.Default.History,
                onClick = onViewVerificationHistory
            )

            MechanicActionCard(
                title = "Manage Inventory",
                description = "Track your parts and tools",
                icon = Icons.Default.Inventory,
                onClick = onManageInventory
            )

            MechanicActionCard(
                title = "Job Listings",
                description = "Browse available repair jobs",
                icon = Icons.AutoMirrored.Filled.ListAlt,  // ✅ Fixed deprecated icon
                onClick = onViewJobs
            )

            MechanicActionCard(
                title = "Diagnostic Tools",
                description = "Access diagnostic resources",
                icon = Icons.Default.Build,
                onClick = onDiagnosticTools
            )
        }
    }
}

@Composable
fun MechanicStatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MechanicActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifiedMechanicDashboardPreview() {
    val fakeDataStore = object : AuthDataStore() {
        override suspend fun saveAuthToken(token: String) {}
        override suspend fun getAuthToken(): String? = null
        override suspend fun clearAuthToken() {}
    }

    com.example.revd_up.ui.theme.REVD_UPTheme {
        VerifiedMechanicDashboard(
            dataStore = fakeDataStore,
            onLogout = {}
        )
    }
}