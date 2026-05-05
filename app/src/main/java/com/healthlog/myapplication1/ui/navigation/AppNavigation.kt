package com.healthlog.myapplication1.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.healthlog.myapplication1.HealthLogApplication
import com.healthlog.myapplication1.ui.screen.calendar.CalendarScreen
import com.healthlog.myapplication1.ui.screen.calendar.CalendarViewModel
import com.healthlog.myapplication1.ui.screen.graph.GraphScreen
import com.healthlog.myapplication1.ui.screen.graph.GraphViewModel
import com.healthlog.myapplication1.ui.screen.home.HomeScreen
import com.healthlog.myapplication1.ui.screen.home.HomeViewModel
import com.healthlog.myapplication1.ui.screen.input.*
import com.healthlog.myapplication1.ui.screen.onboarding.OnboardingScreen
import com.healthlog.myapplication1.ui.screen.onboarding.OnboardingViewModel
import com.healthlog.myapplication1.ui.screen.record.RecordScreen
import com.healthlog.myapplication1.ui.screen.record.RecordViewModel
import com.healthlog.myapplication1.ui.screen.settings.SettingsScreen
import com.healthlog.myapplication1.ui.screen.settings.SettingsViewModel
import com.healthlog.myapplication1.ui.screen.splash.SplashScreen
import com.healthlog.myapplication1.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val app = context.applicationContext as HealthLogApplication
    val c = app.container

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    // 공유 ViewModels (한 번만 생성)
    val weightVm: WeightInputViewModel = viewModel(factory = WeightInputViewModel.Factory(c.weightRepository))
    val bodyFatVm: BodyFatInputViewModel = viewModel(factory = BodyFatInputViewModel.Factory(c.bodyFatRepository))
    val exerciseVm: ExerciseInputViewModel = viewModel(factory = ExerciseInputViewModel.Factory(c.exerciseRepository))
    val mealVm: MealInputViewModel = viewModel(factory = MealInputViewModel.Factory(c.mealRepository, c.aiRepository))

    NavHost(navController = navController, startDestination = NavRoutes.SPLASH) {

        composable(NavRoutes.SPLASH) {
            SplashScreen { _ ->
                coroutineScope.launch {
                    val hasProfile = c.userRepository.hasProfile()
                    val dest = if (hasProfile) NavRoutes.HOME else NavRoutes.ONBOARDING
                    navController.navigate(dest) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            }
        }

        composable(NavRoutes.ONBOARDING) {
            val vm: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory(c.userRepository))
            OnboardingScreen(viewModel = vm) {
                navController.navigate(NavRoutes.HOME) {
                    popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                }
            }
        }

        composable(NavRoutes.HOME) {
            MainScaffold(
                currentRoute = NavRoutes.HOME,
                onTabSelect = { route -> navController.navigateToTab(route) }
            ) {
                val vm: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(
                        c.userRepository, c.weightRepository, c.mealRepository,
                        c.exerciseRepository, c.dailyLogRepository
                    )
                )
                HomeScreen(
                    viewModel = vm,
                    weightInputViewModel = weightVm,
                    bodyFatInputViewModel = bodyFatVm,
                    exerciseInputViewModel = exerciseVm,
                    mealInputViewModel = mealVm,
                    onSettingsClick = { navController.navigate(NavRoutes.SETTINGS) }
                )
            }
        }

        composable(NavRoutes.RECORD) {
            MainScaffold(
                currentRoute = NavRoutes.RECORD,
                onTabSelect = { route -> navController.navigateToTab(route) }
            ) {
                val vm: RecordViewModel = viewModel(
                    factory = RecordViewModel.Factory(
                        c.weightRepository, c.bodyFatRepository,
                        c.mealRepository, c.exerciseRepository
                    )
                )
                RecordScreen(vm, weightVm, bodyFatVm, exerciseVm, mealVm)
            }
        }

        composable(NavRoutes.CALENDAR) {
            MainScaffold(
                currentRoute = NavRoutes.CALENDAR,
                onTabSelect = { route -> navController.navigateToTab(route) }
            ) {
                val vm: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory(c.dailyLogRepository))
                CalendarScreen(viewModel = vm)
            }
        }

        composable(NavRoutes.GRAPH) {
            MainScaffold(
                currentRoute = NavRoutes.GRAPH,
                onTabSelect = { route -> navController.navigateToTab(route) }
            ) {
                val vm: GraphViewModel = viewModel(
                    factory = GraphViewModel.Factory(c.weightRepository, c.bodyFatRepository)
                )
                GraphScreen(viewModel = vm)
            }
        }

        composable(NavRoutes.SETTINGS) {
            MainScaffold(
                currentRoute = NavRoutes.SETTINGS,
                onTabSelect = { route -> navController.navigateToTab(route) }
            ) {
                val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(c.userRepository))
                SettingsScreen(
                    viewModel = vm,
                    onEditProfile = {
                        navController.navigate(NavRoutes.ONBOARDING)
                    }
                )
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

private val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.HOME, "홈", Icons.Default.Home),
    BottomNavItem(NavRoutes.RECORD, "기록", Icons.Default.History),
    BottomNavItem(NavRoutes.CALENDAR, "캘린더", Icons.Default.CalendarMonth),
    BottomNavItem(NavRoutes.GRAPH, "그래프", Icons.Default.ShowChart),
    BottomNavItem(NavRoutes.SETTINGS, "설정", Icons.Default.Settings)
)

@Composable
private fun MainScaffold(
    currentRoute: String,
    onTabSelect: (String) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = BgBase,
        bottomBar = {
            NavigationBar(
                containerColor = BgBase,
                tonalElevation = 0.dp,
                modifier = Modifier.height(64.dp)
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { if (!selected) onTabSelect(item.route) },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenAccent,
                            selectedTextColor = GreenAccent,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = GreenDim
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            content()
        }
    }
}
