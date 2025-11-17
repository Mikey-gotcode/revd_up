package com.example.revd_up.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.revd_up.presentation.views.customer.AddPostRoute

/**
 * Defines all possible bottom navigation destinations, separated by user role.
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String? = null) {
    // Customer Routes
    object CustomerFeed : BottomNavItem("customer_feed", Icons.Default.Home, "Feed")
    // Made title optional, but the label { Text(item.title!!) } fails if null. Setting it explicitly to null.
    object AddPost : BottomNavItem("add_post", Icons.Default.AddCircle, null)
    object CustomerProfile : BottomNavItem("customer_profile", Icons.Default.Person, "Profile")

    // Verified Mechanic Routes
    object MechanicJobs : BottomNavItem("mechanic_jobs", Icons.Default.Construction, "Jobs")
    object MechanicTools : BottomNavItem("mechanic_tools", Icons.Default.Build, "Tools")

    // Admin Routes
    object AdminDashboard : BottomNavItem("admin_dashboard", Icons.Default.AdminPanelSettings, "Dashboard")
    object AdminUsers : BottomNavItem("admin_users", Icons.Default.Person, "Users")
}

// Lists for each role to be passed to the AppBottomNavBar in DashboardScreen
val CustomerNavItems = listOf(
    BottomNavItem.CustomerFeed,
    BottomNavItem.AddPost, // Added to the center
    BottomNavItem.CustomerProfile
)

val VerifiedMechanicNavItems = listOf(
    BottomNavItem.MechanicJobs,
    BottomNavItem.MechanicTools
)

val AdminNavItems = listOf(
    BottomNavItem.AdminDashboard,
    BottomNavItem.AdminUsers
)

/**
 * Reusable Bottom Navigation Bar Composable.
 * @param navController The NavController used to navigate between screens.
 * @param items The list of BottomNavItem applicable to the current user's role.
 */
@Composable
fun AppBottomNavBar(
    navController: NavController,
    items: List<BottomNavItem>
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route ||
                    // Special handling for AddPost: select the icon if the user is in the post flow
                    (item == BottomNavItem.AddPost && (currentRoute?.startsWith("post_") == true))

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title ?: "Add Post") },
                // Use a label only if the title is not null
                label = if (item.title != null) { { Text(item.title) } } else null,
                selected = isSelected,
                onClick = {
                    // Special case: If clicking AddPost, navigate directly to the first step
                    if (item == BottomNavItem.AddPost) {
                        navController.navigate(AddPostRoute.PICK_MEDIA)
                    } else {
                        // Standard navigation logic for Feed and Profile
                        navController.navigate(item.route) {
                            // Keep a single instance of a composable in the back stack
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid re-launching the same destination
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}