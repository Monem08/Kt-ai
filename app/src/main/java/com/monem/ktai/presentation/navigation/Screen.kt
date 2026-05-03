package com.monem.ktai.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object ForgotPassword : Screen("forgot_password")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Security : Screen("security")
    data object WorkspaceList : Screen("workspace_list")
    data object SelectFolder : Screen("select_folder")
    data object FileExplorer : Screen("file_explorer/{workspaceId}") {
        fun createRoute(workspaceId: String) = "file_explorer/$workspaceId"
    }
    data object FileViewer : Screen("file_viewer/{filePath}") {
        fun createRoute(filePath: String) = "file_viewer/${android.net.Uri.encode(filePath)}"
    }
    data object Chat : Screen("chat/{sessionId}") {
        fun createRoute(sessionId: String = "new") = "chat/$sessionId"
    }
    data object DiffPreview : Screen("diff_preview")
    data object ApplyConfirmation : Screen("apply_confirmation")
    data object ChangeHistory : Screen("change_history")
    data object Subscription : Screen("subscription")
    data object UsageLimit : Screen("usage_limit")
}
