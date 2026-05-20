package com.example.finmate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finmate.ui.theme.BackgroundGray
import com.example.finmate.ui.theme.PrimaryBlue
import com.example.finmate.ui.theme.SecondaryBlue
import com.example.finmate.viewmodel.AuthViewModel
import com.example.finmate.viewmodel.FinanceViewModel

sealed class TabScreen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : TabScreen("dashboard", "Home", Icons.Default.Home)
    object History : TabScreen("history", "History", Icons.AutoMirrored.Filled.List)
    object Report : TabScreen("report", "Report", Icons.Default.PieChart)
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
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // FAB only on Dashboard and History
    val showFAB = currentRoute == TabScreen.Dashboard.route || currentRoute == TabScreen.History.route

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(
                    TabScreen.Dashboard,
                    TabScreen.History,
                    TabScreen.Report,
                    TabScreen.Profile
                )
                items.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = selected,
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
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = SecondaryBlue
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFAB,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showSheet = true },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add") }
                )
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
