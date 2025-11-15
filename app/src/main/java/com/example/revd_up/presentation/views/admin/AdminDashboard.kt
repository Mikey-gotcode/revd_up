package com.example.revd_up.presentation.views.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.revd_up.data.store.AuthDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    dataStore: AuthDataStore,
    onLogout: () -> Unit,
    onManageUsers: () -> Unit = {},
    onManageProducts: () -> Unit = {},
    onManageVerification: () -> Unit = {},
    onViewReports: () -> Unit = {},
    onSystemHealth: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            Log.d("AdminDashboard", "Admin logout")
                            dataStore.clearAuthToken()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Log Out")
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

            // ---------------- SUMMARY CARDS ----------------
            Text("Overview", style = MaterialTheme.typography.titleLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(title = "Users", value = "245")
                SummaryCard(title = "Products", value = "1,024")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(title = "Merchants", value = "76")
                SummaryCard(title = "Reports", value = "12")
            }

            Spacer(Modifier.height(12.dp))

            // ---------------- ADMIN ACTIONS ----------------
            Text("Admin Tools", style = MaterialTheme.typography.titleLarge)

            AdminActionCard("Manage Users", onClick = onManageUsers)
            AdminActionCard("Manage Products", onClick = onManageProducts)
            AdminActionCard("Verification Requests", onClick = onManageVerification)
            AdminActionCard("View Reports", onClick = onViewReports)
            AdminActionCard("System Health", onClick = onSystemHealth)
        }
    }
}


@Composable
fun SummaryCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(90.dp),
        shape = RoundedCornerShape(12.dp)
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
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun AdminActionCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    val fake = object : AuthDataStore {
        override suspend fun saveAuthToken(token: String) {}
        override suspend fun getAuthToken(): String? = null
        override suspend fun clearAuthToken() {}
    }

    MaterialTheme {
        AdminDashboard(
            dataStore = fake,
            onLogout = {}
        )
    }
}
