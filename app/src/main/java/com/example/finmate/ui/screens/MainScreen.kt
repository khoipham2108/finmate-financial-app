package com.example.finmate.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finmate.viewmodel.AuthViewModel
import com.example.finmate.viewmodel.FinanceViewModel

sealed class TabScreen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : TabScreen("dashboard", "Dashboard", Icons.Default.Home)
    object History : TabScreen("history", "History", Icons.AutoMirrored.Filled.List)
    object Report : TabScreen("report", "Report", Icons.Default.Menu)
    object Profile : TabScreen("profile", "Profile", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit,
    financeViewModel: FinanceViewModel = viewModel()
) {
    val navController = rememberNavController()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    TabScreen.Dashboard,
                    TabScreen.History,
                    TabScreen.Report,
                    TabScreen.Profile
                )
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TabScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TabScreen.Dashboard.route) {
                DashboardScreen(financeViewModel)
            }
            composable(TabScreen.History.route) {
                HistoryScreen(financeViewModel)
            }
            composable(TabScreen.Report.route) {
                ReportScreen(financeViewModel)
            }
            composable(TabScreen.Profile.route) {
                ProfileScreen(authViewModel, onSignOut)
            }
        }

        if (showSheet) {
            AddTransactionSheet(
                viewModel = financeViewModel,
                onDismiss = { showSheet = false },
                sheetState = sheetState
            )
        }
    }
}
