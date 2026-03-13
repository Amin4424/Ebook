package com.example.ebook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ebook.ui.screens.category.CategoryScreen
import com.example.ebook.ui.screens.home.HomeScreen
import com.example.ebook.ui.screens.reader.ReaderScreen
import com.example.ebook.ui.screens.splash.SplashScreen
import com.example.ebook.ui.screens.wallet.WalletScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onEnterClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                onBookClick = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                }
            )
        }

        composable(Screen.Category.route) {
            CategoryScreen(
                navController = navController,
                onBookClick = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                }
            )
        }

        composable(Screen.Wallet.route) {
            WalletScreen(navController = navController)
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
            ReaderScreen(
                bookId = bookId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
