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
import com.example.projetsy43.ui.theme.screens.AddEventScreen
import com.example.projetsy43.ui.theme.screens.ProfileScreen

@Composable
fun ConcertNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("eventDetail/{cid}") { backStackEntry ->
            //TODO: Here see EventDetailsViewModel todo
            val cid = backStackEntry.arguments?.getString("cid") ?: "0000"
            EventDetailScreen(eventId = cid,navController = navController)
        }
        composable("maps") {MapsScreen(navController)}
        composable("addevent") { AddEventScreen(navController) }

        composable("profile") {
            ProfileScreen(
                navController = navController, // ✅ pass this
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }


    }
}
