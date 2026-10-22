package com.example.connective.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.connective.data.ConnectiveRepository
import com.example.connective.ui.screens.*

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val seekerTabs = listOf(
    Tab("home", "Home", Icons.Default.Home),
    Tab("applications", "Applications", Icons.Default.Shield),
    Tab("messages", "Messages", Icons.Default.Email),
    Tab("profile", "Profile", Icons.Default.Person)
)

private val employerTabs = listOf(
    Tab("emp_post", "Post Job", Icons.Default.Add),
    Tab("emp_applications", "Applications", Icons.Default.Description),
    Tab("emp_candidates", "Candidates", Icons.Default.People),
    Tab("emp_messages", "Messages", Icons.Default.Email),
    Tab("profile", "Profile", Icons.Default.Person)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    val currentUser = ConnectiveRepository.currentUser
    val isLoggedIn = currentUser != null
    val userRole = currentUser?.role ?: "seeker"

    val tabs = when {
        isLoggedIn && userRole == "employer" -> employerTabs
        route in seekerTabs.map { it.route } -> seekerTabs
        route in employerTabs.map { it.route } -> employerTabs
        else -> seekerTabs
    }

    Scaffold(
        bottomBar = {
            if (route != "login" && route != "register" && route != "landing" && route != "role" && route != "setup_profile") {
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
                    onLogin = {
                        val destination = if (ConnectiveRepository.currentUser?.role == "employer") "emp_post" else "setup_profile"
                        navController.navigate(destination) {
                            popUpTo("home")
                        }
                    },
                    onRegister = { navController.navigate("register") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegistered = { role ->
                        val destination = if (role == "employer") "emp_post" else "setup_profile"
                        navController.navigate(destination) {
                            popUpTo("home")
                        }
                    },
                    onLogin = { navController.navigate("login") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("setup_profile") {
                ProfileSetupScreen(
                    onCompleted = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("role") {
                RoleSelectScreen(
                    onSeeker = {
                        navController.navigate("setup_profile") { popUpTo("home") { inclusive = true } }
                    },
                    onEmployer = {
                        navController.navigate("emp_post") { popUpTo("home") }
                    }
                )
            }

            // Job seeker
            composable("home") {
                HomeScreen(
                    isLoggedIn = isLoggedIn,
                    onSignInClick = { navController.navigate("login") },
                    onJobClick = { id -> navController.navigate("job/$id") }
                )
            }
            composable("applications") { ApplicationsScreen() }
            composable("messages") { MessagesScreen() }
            composable("profile") {
                ProfileScreen(
                    isLoggedIn = isLoggedIn,
                    onSignInClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") },
                    onSignOutClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = "job/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                JobDetailsScreen(
                    jobId = entry.arguments?.getInt("id") ?: 1,
                    isLoggedIn = isLoggedIn,
                    onRequireAuth = { navController.navigate("login") },
                    onBack = { navController.popBackStack() }
                )
            }

            // Employer
            composable("emp_post") {
                PostJobScreen(
                    onJobPosted = {
                        navController.navigate("home")
                    }
                )
            }
            composable("emp_applications") { ApplicationsScreen() }
            composable("emp_candidates") { CandidatesScreen() }
            composable("emp_messages") { MessagesScreen() }
        }
    }
}