package com.example.ebook.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.ebook.ui.screens.bookdetails.BookDetailsScreen
import com.example.ebook.ui.screens.category.CategoryScreen
import com.example.ebook.ui.screens.home.HomeScreen
import com.example.ebook.ui.screens.library.LibraryScreen
import com.example.ebook.ui.screens.reader.ReaderScreen
import com.example.ebook.ui.screens.splash.SplashScreen
import com.example.ebook.ui.screens.wallet.WalletScreen
import com.example.ebook.ui.screens.community.CommunityScreen

private const val ANIM_DURATION = 350

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash,
            enterTransition = { slideInHorizontally(tween(ANIM_DURATION)) { it } + fadeIn(tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(tween(ANIM_DURATION)) { -it } + fadeOut(tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(tween(ANIM_DURATION)) { -it } + fadeIn(tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(tween(ANIM_DURATION)) { it } + fadeOut(tween(ANIM_DURATION)) }
        ) {
            composable<Screen.Splash>(
                enterTransition = { fadeIn(tween(ANIM_DURATION)) },
                exitTransition = { fadeOut(tween(ANIM_DURATION)) }
            ) {
                SplashScreen(
                    onEnterClick = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.Home> {
                HomeScreen(
                    navController = navController,
                    onBookClick = { bookId -> navController.navigate(Screen.BookDetails(bookId)) },
                )
            }

            composable<Screen.Category> {
                CategoryScreen(
                    navController = navController,
                    onBookClick = { bookId -> navController.navigate(Screen.BookDetails(bookId)) }
                )
            }

            composable<Screen.Community> { CommunityScreen(navController) }
            composable<Screen.Wallet> {
                WalletScreen(navController = navController)
            }

            composable<Screen.Library> {
                LibraryScreen(
                    navController = navController,
                    onBookClick = { bookId -> navController.navigate(Screen.BookDetails(bookId)) }
                )
            }

            composable<Screen.BookDetails> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.BookDetails>()
                BookDetailsScreen(
                    bookId = args.bookId,
                    onBackClick = { navController.popBackStack() },
                    onReadClick = { navController.navigate(Screen.Reader(args.bookId)) },
                )
            }

            composable<Screen.Reader> { backStackEntry ->
                val args = backStackEntry.toRoute<Screen.Reader>()
                ReaderScreen(
                    bookId = args.bookId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

