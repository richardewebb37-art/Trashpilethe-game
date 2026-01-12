package com.trashapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trashapp.ui.screens.GameScreen
import com.trashapp.ui.screens.MainMenuScreen
import com.trashapp.ui.screens.SettingsScreen

@Composable
fun TrashNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onPlayClick = { navController.navigate(Screen.Game.route) },
                onQuickMatchClick = { navController.navigate(Screen.Game.route) },
                onMultiplayerClick = { /* TODO: Implement multiplayer screen */ },
                onProfileClick = { /* TODO: Implement profile screen */ },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Game.route) {
            GameScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Game : Screen("game")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object Skills : Screen("skills")
    object Abilities : Screen("abilities")
    object Multiplayer : Screen("multiplayer")
}