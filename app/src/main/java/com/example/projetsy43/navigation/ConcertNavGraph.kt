package com.example.projetsy43.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projetsy43.ui.theme.EventDetailScreen
import com.example.projetsy43.ui.theme.HomeScreen
import com.example.projetsy43.ui.theme.LoginScreen
import com.example.projetsy43.ui.theme.MapsScreen
import com.example.projetsy43.ui.theme.RegisterScreen
import com.example.projetsy43.ui.theme.WelcomeScreen

@Composable
fun ConcertNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("eventDetail/{title}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Unknown Event"
            EventDetailScreen(title = title)
        }
        composable("maps") {MapsScreen(navController)}




    }
}
