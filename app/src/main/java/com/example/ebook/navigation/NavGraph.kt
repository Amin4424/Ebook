package com.example.ebook.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ebook.ui.screens.category.CategoryScreen
import com.example.ebook.ui.screens.home.HomeScreen
import com.example.ebook.ui.screens.library.LibraryScreen
import com.example.ebook.ui.screens.reader.ReaderScreen
import com.example.ebook.ui.screens.splash.SplashScreen
import com.example.ebook.ui.screens.wallet.WalletScreen

private const val ANIM_DURATION = 350

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { slideInHorizontally(tween(ANIM_DURATION)) { it } + fadeIn(tween(ANIM_DURATION)) },
        exitTransition = { slideOutHorizontally(tween(ANIM_DURATION)) { -it } + fadeOut(tween(ANIM_DURATION)) },
        popEnterTransition = { slideInHorizontally(tween(ANIM_DURATION)) { -it } + fadeIn(tween(ANIM_DURATION)) },
        popExitTransition = { slideOutHorizontally(tween(ANIM_DURATION)) { it } + fadeOut(tween(ANIM_DURATION)) }
    ) {
        composable(
            Screen.Splash.route,
            enterTransition = { fadeIn(tween(ANIM_DURATION)) },
            exitTransition = { fadeOut(tween(ANIM_DURATION)) }
        ) {
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

        composable(Screen.Library.route) {
            LibraryScreen(
                navController = navController,
                onBookClick = { bookId ->
                    navController.navigate(Screen.Reader.createRoute(bookId))
                }
            )
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
