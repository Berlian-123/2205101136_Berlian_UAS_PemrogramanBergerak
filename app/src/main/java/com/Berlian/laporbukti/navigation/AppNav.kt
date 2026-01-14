package com.Berlian.laporbukti.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Berlian.laporbukti.data.ReportRepository
import com.Berlian.laporbukti.ui.*

@Composable
fun AppNav(repo: ReportRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login.route) {

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                onCreateReport = { navController.navigate(Routes.Create.route) },
                onHistory = { navController.navigate(Routes.History.route) },
                onStatistics = { navController.navigate(Routes.Statistics.route) }, // Rute Baru
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Create.route) {
            CreateReportScreen(
                repo = repo,
                onBack = { navController.popBackStack() },
                onSaved = { id ->
                    navController.navigate(Routes.Detail.build(id)) {
                        popUpTo(Routes.Create.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.History.route) {
            HistoryScreen(
                repo = repo,
                onBack = { navController.popBackStack() },
                onOpenDetail = { id -> navController.navigate(Routes.Detail.build(id)) }
            )
        }

        // Screen Statistik (Baru)
        composable(Routes.Statistics.route) {
            StatisticsScreen(
                repo = repo,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DetailScreen(
                repo = repo,
                reportId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
