package com.richi_mc.myapplication.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.richi_mc.myapplication.R
import com.richi_mc.myapplication.ui.presentation.history.LlmScreen
import com.richi_mc.myapplication.ui.presentation.history.MisTicketsScreen
import com.richi_mc.myapplication.ui.presentation.home.HomeScreen
import com.richi_mc.myapplication.ui.presentation.profile.ProfileScreen
import com.richi_mc.myapplication.ui.presentation.record.SpeechScreen
import com.richi_mc.myapplication.ui.presentation.scanner.OcrScreen

private data class BottomNavItem<T : Any>(
    val name: String,
    val route: T,
    val icon: ImageVector
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FinantialNavigationWrapper() {

    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(stringResource(R.string.nav_bar_home),    Main,    Icons.Default.Home),
        BottomNavItem(stringResource(R.string.nav_bar_history), History, Icons.AutoMirrored.Filled.List),
        BottomNavItem(stringResource(R.string.nav_bar_scan),    Scan,  Icons.Default.DocumentScanner),
        BottomNavItem(stringResource(R.string.nav_bar_record),  Record,    Icons.Default.Mic),
        BottomNavItem(stringResource(R.string.nav_bar_profile), Profile,    Icons.Default.Person)
    )

    Scaffold(
        contentWindowInsets = WindowInsets(top = 0.dp),
        bottomBar = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp
            ) {
                items.forEach { item ->
                    val isSelected = currentDestination?.hasRoute(item.route::class) == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { 
                            Icon(
                                imageVector = item.icon, 
                                contentDescription = item.name
                            ) 
                        },
                        label = { 
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Main,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            composable<Main> {
                HomeScreen()
            }
            composable<History> {
                MisTicketsScreen()
            }
            composable<Scan> {
                OcrScreen()
            }
            composable<Record> {
                SpeechScreen()
            }
            composable<Profile> {
                ProfileScreen()
            }
        }
    }
}
