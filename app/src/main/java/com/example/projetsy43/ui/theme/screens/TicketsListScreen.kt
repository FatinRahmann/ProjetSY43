package com.example.projetsy43.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projetsy43.R
import com.example.projetsy43.factory.TicketListViewModelFactory
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.ui.theme.components.TicketComponent
import com.example.projetsy43.viewmodel.TicketListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsListScreen(
    navController: NavController
) {
    val ticketRepo = remember { TicketRepository() }
    val eventRepo = remember { EventRepository() }
    val viewModel: TicketListViewModel = viewModel(
        factory = TicketListViewModelFactory(ticketRepo, eventRepo)
    )

    val ticketsList = viewModel.toShow

    LaunchedEffect(Unit) {
        viewModel.toShow.clear()
        viewModel.getTickets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My tickets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_retour),
                        contentDescription = "return",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable { navController.navigate("home") {
                                popUpTo("home") { inclusive = true } // Clears back stack up to home
                                launchSingleTop = true
                            } }
                            .size(28.dp),
                        tint = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(ticketsList) { ticket ->
                val event = remember { mutableStateOf<Event?>(null) }

                LaunchedEffect(ticket.concert_id) {
                    event.value = viewModel.getEventData(ticket.concert_id)
                }

                event.value?.let {
                    TicketComponent(event = it) {
                        val ticketCount = viewModel.getTicketCountPerCid(it.cid)
                        navController.navigate("ticketqrcode/${ticketCount}/${it.name}/${it.address}/${it.datetime}")
                    }
                }

            }
        }
    }
}