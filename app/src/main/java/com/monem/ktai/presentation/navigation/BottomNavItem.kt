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
    val icon: ImageVector,
    val label: String,
) {
    data object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home")
    data object Chat : BottomNavItem(Screen.Chat.createRoute(), Icons.Default.Chat, "Chat")
    data object Files : BottomNavItem(Screen.WorkspaceList.route, Icons.Default.Folder, "Files")
    data object History : BottomNavItem(Screen.ChangeHistory.route, Icons.Default.History, "History")
    data object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Chat,
    BottomNavItem.Files,
    BottomNavItem.History,
    BottomNavItem.Profile,
)
