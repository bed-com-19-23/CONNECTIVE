package com.example.connective.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.screens.*
import com.example.connective.viewmodel.*
import kotlinx.coroutines.launch

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val seekerTabs = listOf(
    Tab("home", "Home", Icons.Default.Home),
    Tab("applications", "Applications", Icons.AutoMirrored.Filled.Assignment),
    Tab("messages", "Messages", Icons.Default.Email),
    Tab("profile", "Profile", Icons.Default.Person)
)

private val employerTabs = listOf(
    Tab("home", "Home", Icons.Default.Home),
    Tab("emp_post", "Post Job", Icons.Default.Add),
    Tab("emp_candidates", "Candidates", Icons.Default.People),
    Tab("messages", "Messages", Icons.Default.Email),
    Tab("profile", "Profile", Icons.Default.Person)
)

private val adminTabs = listOf(
    Tab("admin_dashboard", "Admin", Icons.Default.AdminPanelSettings),
    Tab("home", "Home", Icons.Default.Home),
    Tab("emp_post", "Post Job", Icons.Default.Add),
    Tab("messages", "Messages", Icons.Default.Email),
    Tab("profile", "Profile", Icons.Default.Person)
)

@Composable
fun AppNavigation(
    appViewModel: AppViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    jobDetailsViewModel: JobDetailsViewModel = viewModel(),
    applicationsViewModel: ApplicationsViewModel = viewModel(),
    candidatesViewModel: CandidatesViewModel = viewModel(),
    messagesViewModel: MessagesViewModel = viewModel(),
    notificationsViewModel: NotificationsViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val currentUser by appViewModel.currentUser.collectAsState()
    val unreadNotifCount by appViewModel.unreadNotifCount.collectAsState()
    val employerJobs by homeViewModel.openJobs.collectAsState()
    val savedJobIds by homeViewModel.savedJobIds.collectAsState()
    val savedJobsList by homeViewModel.openJobs.collectAsState()
    val savedJobsFiltered = savedJobsList.filter { savedJobIds.contains(it.id) }
    val seekerApplications by applicationsViewModel.applications.collectAsState()

    val isLoggedIn = currentUser != null
    val userRole = currentUser?.role ?: "seeker"

    // Request POST_NOTIFICATIONS permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val tabs = when {
        isLoggedIn && userRole == "admin" -> adminTabs
        isLoggedIn && userRole == "employer" -> employerTabs
        else -> seekerTabs
    }

    Scaffold(
        bottomBar = {
            if (route != "login" && route != "register" && route != "landing" && route != "setup_profile" && route != "notifications") {
                NavigationBar(containerColor = Color.White) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(tabs.first().route)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("landing") {
                LandingScreen(
                    onGetStarted = { navController.navigate("register") },
                    onLogin = { navController.navigate("login") }
                )
            }

            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        val destination = when (currentUser?.role) {
                            "admin" -> "admin_dashboard"
                            "employer" -> "emp_post"
                            else -> "home"
                        }
                        navController.navigate(destination) {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onRegister = { navController.navigate("register") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegistered = { role ->
                        val destination = if (role == "employer") "emp_post" else "setup_profile"
                        navController.navigate(destination) {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onLogin = { navController.navigate("login") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("setup_profile") {
                ProfileSetupScreen(
                    currentUser = currentUser,
                    viewModel = profileViewModel,
                    onCompleted = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("notifications") {
                NotificationsScreen(
                    viewModel = notificationsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("admin_dashboard") {
                AdminScreen(viewModel = adminViewModel)
            }

            // Home / Jobs
            composable("home") {
                HomeScreen(
                    currentUser = currentUser,
                    unreadNotifCount = unreadNotifCount,
                    onNotificationsClick = { navController.navigate("notifications") },
                    onSignInClick = { navController.navigate("login") },
                    onJobClick = { id -> navController.navigate("job/$id") },
                    viewModel = homeViewModel
                )
            }

            composable("applications") {
                ApplicationsScreen(viewModel = applicationsViewModel)
            }

            composable("messages") {
                MessagesScreen(
                    currentUser = currentUser,
                    viewModel = messagesViewModel
                )
            }

            composable("profile") {
                ProfileScreen(
                    currentUser = currentUser,
                    applications = seekerApplications,
                    savedJobs = savedJobsFiltered,
                    onSignInClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") },
                    onSignOutClick = {
                        appViewModel.logout()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    viewModel = profileViewModel
                )
            }

            composable(
                route = "job/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val jobId = entry.arguments?.getString("id") ?: ""
                JobDetailsScreen(
                    jobId = jobId,
                    currentUser = currentUser,
                    savedJobIds = savedJobIds,
                    onToggleSave = { jId, _ ->
                        homeViewModel.toggleSaveJob(jId, currentUser)
                    },
                    onRequireAuth = { navController.navigate("login") },
                    onBack = { navController.popBackStack() },
                    viewModel = jobDetailsViewModel
                )
            }

            // Employer
            composable("emp_post") {
                PostJobScreen(
                    currentUser = currentUser,
                    employerJobs = employerJobs.filter { it.employerId == currentUser?.uid },
                    onPostJob = { title, comp, loc, type, cat, sal, edu, exp, desc ->
                        val empId = currentUser?.uid ?: return@PostJobScreen
                        coroutineScope.launch {
                            ConnectiveRepository.addJob(
                                title = title,
                                company = comp,
                                employerId = empId,
                                location = loc,
                                type = type,
                                category = cat,
                                salary = sal,
                                minEducation = edu,
                                minExperienceYears = exp,
                                description = desc
                            )
                        }
                    },
                    onCloseJob = { jId ->
                        coroutineScope.launch {
                            ConnectiveRepository.closeJob(jId)
                        }
                    },
                    onDeleteJob = { jId ->
                        coroutineScope.launch {
                            ConnectiveRepository.deleteJob(jId)
                        }
                    }
                )
            }

            composable("emp_candidates") {
                CandidatesScreen(
                    viewModel = candidatesViewModel,
                    onStartConversation = { partnerUid, partnerName, jId ->
                        val user = currentUser
                        if (user != null) {
                            messagesViewModel.startConversationWithUser(
                                currentUser = user,
                                partnerUid = partnerUid,
                                partnerName = partnerName,
                                jobId = jId
                            ) {
                                navController.navigate("messages")
                            }
                        }
                    }
                )
            }
        }
    }
}
