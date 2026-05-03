package com.monem.ktai.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.monem.ktai.presentation.auth.ForgotPasswordScreen
import com.monem.ktai.presentation.auth.LoginScreen
import com.monem.ktai.presentation.auth.SignupScreen
import com.monem.ktai.presentation.chat.ChatScreen
import com.monem.ktai.presentation.common.theme.CardBackground
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.Surface
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextTertiary
import com.monem.ktai.presentation.dashboard.HomeScreen
import com.monem.ktai.presentation.diff.ApplyConfirmationScreen
import com.monem.ktai.presentation.diff.DiffPreviewScreen
import com.monem.ktai.presentation.explorer.FileExplorerScreen
import com.monem.ktai.presentation.explorer.FileViewerScreen
import com.monem.ktai.presentation.history.ChangeHistoryScreen
import com.monem.ktai.presentation.onboarding.OnboardingScreen
import com.monem.ktai.presentation.onboarding.SplashScreen
import com.monem.ktai.presentation.profile.ProfileScreen
import com.monem.ktai.presentation.profile.SecurityScreen
import com.monem.ktai.presentation.profile.SettingsScreen
import com.monem.ktai.presentation.subscription.SubscriptionScreen
import com.monem.ktai.presentation.subscription.UsageLimitScreen
import com.monem.ktai.presentation.workspace.SelectFolderScreen
import com.monem.ktai.presentation.workspace.WorkspaceListScreen

private val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun KtAINavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = CardBackground) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = TextPrimary,
                                unselectedIconColor = TextTertiary,
                                unselectedTextColor = TextTertiary,
                                indicatorColor = Primary.copy(alpha = 0.12f),
                            ),
                            onClick = {
                                navController.navigate(item.navigationRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Signup.route) {
                SignupScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToChat = { navController.navigate(Screen.Chat.createRoute()) },
                    onNavigateToWorkspace = { navController.navigate(Screen.WorkspaceList.route) },
                    onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) },
                    onNavigateToUsage = { navController.navigate(Screen.UsageLimit.route) },
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSecurity = { navController.navigate(Screen.Security.route) },
                    onNavigateToSubscription = { navController.navigate(Screen.Subscription.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Security.route) {
                SecurityScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.WorkspaceList.route) {
                WorkspaceListScreen(
                    onNavigateToSelectFolder = { navController.navigate(Screen.SelectFolder.route) },
                    onNavigateToExplorer = { workspaceId ->
                        navController.navigate(Screen.FileExplorer.createRoute(workspaceId))
                    },
                )
            }

            composable(Screen.SelectFolder.route) {
                SelectFolderScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onFolderSelected = { navController.popBackStack() },
                )
            }

            composable(
                route = Screen.FileExplorer.route,
                arguments = listOf(navArgument("workspaceId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val workspaceId = backStackEntry.arguments?.getString("workspaceId") ?: return@composable
                FileExplorerScreen(
                    workspaceId = workspaceId,
                    onNavigateToFileViewer = { filePath ->
                        navController.navigate(Screen.FileViewer.createRoute(filePath))
                    },
                    onNavigateToChat = { navController.navigate(Screen.Chat.createRoute()) },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Screen.FileViewer.route,
                arguments = listOf(navArgument("filePath") { type = NavType.StringType }),
            ) { backStackEntry ->
                val filePath = backStackEntry.arguments?.getString("filePath") ?: return@composable
                FileViewerScreen(
                    filePath = java.net.URLDecoder.decode(filePath, "UTF-8"),
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("sessionId") {
                    type = NavType.StringType
                    defaultValue = "new"
                }),
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: "new"
                ChatScreen(
                    sessionId = sessionId,
                    onNavigateToDiff = { navController.navigate(Screen.DiffPreview.route) },
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable(Screen.DiffPreview.route) {
                DiffPreviewScreen(
                    onApply = { navController.navigate(Screen.ApplyConfirmation.route) },
                    onCancel = { navController.popBackStack() },
                )
            }

            composable(Screen.ApplyConfirmation.route) {
                ApplyConfirmationScreen(
                    onConfirm = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.DiffPreview.route) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() },
                )
            }

            composable(Screen.ChangeHistory.route) {
                ChangeHistoryScreen(
                    onNavigateToDiff = { navController.navigate(Screen.DiffPreview.route) },
                )
            }

            composable(Screen.Subscription.route) {
                SubscriptionScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.UsageLimit.route) {
                UsageLimitScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
