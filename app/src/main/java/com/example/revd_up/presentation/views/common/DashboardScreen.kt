package com.example.revd_up.presentation.views.common

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.revd_up.UserType
import com.example.revd_up.data.api.RevdUpPostService
import com.example.revd_up.data.store.AuthDataStore
import com.example.revd_up.presentation.navigation.*
import com.example.revd_up.presentation.views.admin.AdminDashboard
//import com.example.revd_up.presentation.views.admin.AdminUsersScreen
import com.example.revd_up.presentation.views.customer.AddPostRoute
import com.example.revd_up.presentation.views.customer.CaptionDetailsScreen
import com.example.revd_up.presentation.views.customer.CustomerFeedScreen
//import com.example.revd_up.presentation.views.customer.CustomerProfileScreen
import com.example.revd_up.presentation.views.customer.FilterEditScreen
import com.example.revd_up.presentation.views.customer.PickMediaScreen
//import com.example.revd_up.presentation.views.mechanic.MechanicJobsScreen
import com.example.revd_up.presentation.views.mechanic.VerifiedMechanicDashboard
import kotlinx.coroutines.launch

private const val TAG = "REVD_UP_DASHBOARD"

/**
 * The main shell for all authenticated users.
 * It determines the correct bottom navigation bar and content based on the user's role.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dataStore: AuthDataStore,
    userType: UserType,
    postService: RevdUpPostService,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 1. Determine navigation items and start destination based on role
    val (navItems, startDestination) = when (userType) {
        UserType.CUSTOMER -> CustomerNavItems to BottomNavItem.CustomerFeed.route
        UserType.ADMIN -> AdminNavItems to BottomNavItem.AdminDashboard.route
        UserType.VERIFIED_MECHANIC -> VerifiedMechanicNavItems to BottomNavItem.MechanicJobs.route
        UserType.UNKNOWN -> emptyList<BottomNavItem>() to ""
    }

    if (userType == UserType.UNKNOWN) {
        UnknownUserScreen(dataStore = dataStore, onLogout = onLogout)
        return
    }

    // Check if the current route is part of the main bottom navigation (Feed or Profile)
    // The AddPost placeholder route is no longer checked here.
    val isBottomBarVisible = navItems.map { it.route }.contains(currentRoute)

    // 2. Main Scaffold (Shell)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REV'D UP (${userType.name})") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            Log.d(TAG, "Logout initiated from TopAppBar.")
                            dataStore.clearAuthToken()
                            dataStore.clearUserRole()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Log Out")
                    }
                }
            )
        },
        bottomBar = {
            // Only show the bottom bar if the user is on a main navigation screen
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                AppBottomNavBar(navController = navController, items = navItems)
            }
        }
    ) { paddingValues ->
        // 3. Navigation Host for all Screens
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // --- MAIN BOTTOM NAV ROUTES ---

            // This route now points directly to the PickMediaScreen,
            // but is only reached if the user navigates there explicitly (which they shouldn't).
            // The logic in AppBottomNavBar bypasses this and goes to PICK_MEDIA.
            // However, we MUST keep a composable defined for every route listed in BottomNavItem.
            composable(BottomNavItem.AddPost.route) {
                // The user should never land here, but it must be defined to prevent the crash
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Starting Post Flow...")
                    // Immediately navigate to the real start of the flow
                    LaunchedEffect(Unit) {
                        navController.navigate(AddPostRoute.PICK_MEDIA) {
                            popUpTo(BottomNavItem.AddPost.route) { inclusive = true }
                        }
                    }
                }
            }

            // Customer Routes
            composable(BottomNavItem.CustomerFeed.route) {
                CustomerFeedScreen(postService = postService)
            }
//            composable(BottomNavItem.CustomerProfile.route) {
//                CustomerProfileScreen()
//            }

            // Admin Routes
            composable(BottomNavItem.AdminDashboard.route) {
                AdminDashboard(dataStore, onLogout)
            }
//            composable(BottomNavItem.AdminUsers.route) {
//                AdminUsersScreen()
//            }

//            // Verified Mechanic Routes
//            composable(BottomNavItem.MechanicJobs.route) {
//                MechanicJobsScreen()
//            }
            composable(BottomNavItem.MechanicTools.route) {
                VerifiedMechanicDashboard(
                    dataStore,
                    onLogout = onLogout
                )
            }

            // --- ADD POST FLOW ROUTES ---

            // Step 1: Pick Media (Actual Start of the multi-step flow)
            composable(AddPostRoute.PICK_MEDIA) {
                PickMediaScreen(
                    onMediaPicked = { uri ->
                        navController.navigate(AddPostRoute.createFilterEditRoute(uri))
                    },
                    onBack = { navController.popBackStack(BottomNavItem.CustomerFeed.route, inclusive = false) }
                )
            }

            // Step 2: Filter/Edit
            composable(
                route = AddPostRoute.FILTER_EDIT,
                arguments = listOf(navArgument("mediaUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("mediaUri")
                val mediaUri = uriString?.let { Uri.parse(it) } ?: run {
                    Log.e(TAG, "Media URI not found in arguments.")
                    navController.popBackStack()
                    return@composable
                }
                FilterEditScreen(
                    mediaUri = mediaUri,
                    onNext = { filterIndex ->
                        navController.navigate(AddPostRoute.createCaptionDetailsRoute(mediaUri, filterIndex))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Step 3: Caption/Details
            composable(
                route = AddPostRoute.CAPTION_DETAILS,
                arguments = listOf(
                    navArgument("mediaUri") { type = NavType.StringType },
                    navArgument("filterIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("mediaUri")
                val filterIndex = backStackEntry.arguments?.getInt("filterIndex") ?: 0
                val mediaUri = uriString?.let { Uri.parse(it) } ?: run {
                    Log.e(TAG, "Media URI not found in arguments for final step.")
                    navController.popBackStack()
                    return@composable
                }

                CaptionDetailsScreen(
                    mediaUri = mediaUri,
                    filterIndex = filterIndex,
                    postService = postService,
                    onPostSuccess = {
                        // Navigate back to the main feed after posting
                        navController.popBackStack(BottomNavItem.CustomerFeed.route, inclusive = false)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun UnknownUserScreen(
    dataStore: AuthDataStore,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Error: Unknown User Role",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your assigned role is unknown. Please log out and try again.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                scope.launch {
                    Log.d(TAG, "Logout from UnknownUserScreen.")
                    dataStore.clearAuthToken()
                    dataStore.clearUserRole()
                    onLogout()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }
    }
}