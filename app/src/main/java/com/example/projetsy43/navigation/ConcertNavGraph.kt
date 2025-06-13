package com.example.projetsy43.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.projetsy43.factory.HomeViewModelFactory
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.ui.theme.EventDetailScreen
import com.example.projetsy43.ui.theme.HomeScreen
import com.example.projetsy43.ui.theme.LoginScreen
import com.example.projetsy43.ui.theme.MapsScreen
import com.example.projetsy43.ui.theme.RegisterScreen
import com.example.projetsy43.ui.theme.WelcomeScreen
import com.example.projetsy43.ui.theme.screens.AddEventScreen
import com.example.projetsy43.ui.theme.screens.FakePayment
import com.example.projetsy43.ui.theme.screens.FavoritesScreen
import com.example.projetsy43.ui.theme.screens.ProfileScreen
import com.example.projetsy43.ui.theme.screens.TicketQrViewScreen
import com.example.projetsy43.ui.theme.screens.TicketsListScreen
import com.example.projetsy43.viewmodel.HomeViewModel


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
                navController = navController,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("favorites") {
            val context = LocalContext.current
            val repository = remember { EventRepository() }
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )


            LaunchedEffect(Unit) {
                homeViewModel.fetchEvents()
            }

            val allEvents = homeViewModel.allEvents

            FavoritesScreen(
                allEvents = allEvents,
                onEventClick = { event ->
                    navController.navigate("eventDetail/${event.cid}")
                }
            )
        }

        composable("ticketslist") { TicketsListScreen(navController) }
        composable("ticketqrcode/{ticketnumber}/{eventname}/{addr}/{date}") { backStackEntry ->
            val ticketnumber = (backStackEntry.arguments?.getString("ticketnumber") ?: "0").toInt()
            val eventname = backStackEntry.arguments?.getString("eventname") ?: "No name"
            val addr = backStackEntry.arguments?.getString("addr") ?: "No address"
            val date = backStackEntry.arguments?.getString("date") ?: "No date"
            TicketQrViewScreen(ticketnumber, eventname, addr, date, navController) }

        composable(
            route = "fakepayment/{eventId}/{amount}/{price}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.IntType },
                navArgument("price") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")!!
            val amount = backStackEntry.arguments?.getInt("amount")!!
            val price = backStackEntry.arguments?.getFloat("price")!!

            FakePayment(eventId = eventId, amount = amount, price = price, navController = navController)
        }

    }
}