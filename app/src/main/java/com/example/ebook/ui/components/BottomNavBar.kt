package com.example.ebook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ebook.navigation.Screen
import com.example.ebook.ui.theme.*
import kotlin.reflect.KClass

data class BottomNavItem<T : Any>(
    val label: String,
    val route: T,
    val routeClass: KClass<T>,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem("خانه", Screen.Home, Screen.Home::class, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("کاوش", Screen.Category, Screen.Category::class, Icons.Filled.Explore, Icons.Outlined.Explore),
        BottomNavItem("انجمن", Screen.Community, Screen.Community::class, Icons.Filled.People, Icons.Outlined.PeopleOutline),
        BottomNavItem("کتاب‌هایم", Screen.Library, Screen.Library::class, Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks),
        BottomNavItem("کیف پول", Screen.Wallet, Screen.Wallet::class, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.routeClass) } == true
                if (index == 0) {
                    FloatingActionButton(
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = Gold400,
                        contentColor = Navy900,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp)
                    ) {
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Gold500,
                            selectedTextColor = Gold500,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}

