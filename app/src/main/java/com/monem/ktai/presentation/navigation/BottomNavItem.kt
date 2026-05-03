package com.monem.ktai.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val navigationRoute: String = route,
    val icon: ImageVector,
    val label: String,
) {
    data object Home : BottomNavItem(route = Screen.Home.route, icon = Icons.Default.Home, label = "Home")
    data object Chat : BottomNavItem(
        route = Screen.Chat.route,
        navigationRoute = Screen.Chat.createRoute(),
        icon = Icons.Default.Chat,
        label = "Chat",
    )
    data object Files : BottomNavItem(route = Screen.WorkspaceList.route, icon = Icons.Default.Folder, label = "Files")
    data object History : BottomNavItem(route = Screen.ChangeHistory.route, icon = Icons.Default.History, label = "History")
    data object Profile : BottomNavItem(route = Screen.Profile.route, icon = Icons.Default.Person, label = "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Chat,
    BottomNavItem.Files,
    BottomNavItem.History,
    BottomNavItem.Profile,
)
