package com.binarybhaskar.branchit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.binarybhaskar.branchit.screens.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar

enum class MainScreen(val label: String) {
    Home("Home"),
    Connect("Connect"),
    Post("Post"),
    Chat("Chat"),
    Profile("Profile"),
    Updates("Updates") // Hidden, only for notification
}

// Multiplatform device size detection
@Composable
expect fun isLargeScreen(): Boolean

private val screenIcons = mapOf(
    MainScreen.Home to "\uD83C\uDFE0",      // House
    MainScreen.Connect to "\uD83D\uDC65",   // People
    MainScreen.Post to "✏️",                // Pencil
    MainScreen.Chat to "\uD83D\uDCAC",      // Chat bubble
    MainScreen.Profile to "\uD83D\uDC64"    // Person
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    // Simulate login state (replace with persistent logic as needed)
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var selectedScreen by rememberSaveable { mutableStateOf(MainScreen.Home) }

    if (!isLoggedIn) {
        LoginScreen(
            isLoading = false,
            onGoogleLogin = { isLoggedIn = true }
        )
    } else {
        val largeScreen = isLargeScreen()
        val navScreens = listOf(
            MainScreen.Home,
            MainScreen.Connect,
            MainScreen.Post,
            MainScreen.Chat,
            MainScreen.Profile
        )
        val showUpdates = selectedScreen == MainScreen.Updates
        val showTopBar = selectedScreen != MainScreen.Updates
        if (largeScreen) {
            Row(Modifier.fillMaxSize()) {
                NavigationRail {
                    navScreens.forEach { screen ->
                        NavigationRailItem(
                            selected = selectedScreen == screen,
                            onClick = { selectedScreen = screen },
                            label = { Text(screen.label) },
                            icon = { Text(screenIcons[screen] ?: "") }
                        )
                    }
                }
                Column(Modifier.weight(1f)) {
                    if (showTopBar) {
                        TopAppBar(
                            title = { Text("BranchIT") },
                            actions = {
                                IconButton(onClick = { selectedScreen = MainScreen.Updates }) {
                                    Text("🔔", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                                }
                            }
                        )
                    }
                    Box(Modifier.fillMaxSize()) {
                        if (showUpdates) {
                            UpdatesScreen()
                        } else {
                            MainScreenContent(selectedScreen)
                        }
                    }
                }
            }
        } else {
            Scaffold(
                topBar = {
                    if (showTopBar) {
                        TopAppBar(
                            title = { Text("BranchIT") },
                            actions = {
                                IconButton(onClick = { selectedScreen = MainScreen.Updates }) {
                                    Text("🔔", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                                }
                            }
                        )
                    }
                },
                bottomBar = {
                    NavigationBar {
                        navScreens.forEach { screen ->
                            NavigationBarItem(
                                selected = selectedScreen == screen,
                                onClick = { selectedScreen = screen },
                                label = { Text(screen.label) },
                                icon = { Text(screenIcons[screen] ?: "") }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(Modifier.padding(innerPadding).fillMaxSize()) {
                    if (showUpdates) {
                        UpdatesScreen()
                    } else {
                        MainScreenContent(selectedScreen)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContent(screen: MainScreen) {
    when (screen) {
        MainScreen.Home -> HomeScreen()
        MainScreen.Connect -> SettingsScreen()
        MainScreen.Post -> PostScreen()
        MainScreen.Chat -> ChatScreen()
        MainScreen.Profile -> ProfileScreen()
        else -> {}
    }
}