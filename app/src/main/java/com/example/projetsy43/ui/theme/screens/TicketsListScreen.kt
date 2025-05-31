package com.example.projetsy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projetsy43.factory.TicketListViewModelFactory
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.viewmodel.TicketListViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.ui.theme.components.TicketComponent
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TicketsListScreen(
    navController: NavController
) {
    val ticketRepo = remember { TicketRepository() }
    val eventRepo = remember { EventRepository() }
    val viewModel: TicketListViewModel = viewModel(
        factory = TicketListViewModelFactory(ticketRepo, eventRepo)
    )

    var ticketsList = viewModel.toShow

    LaunchedEffect(Unit) {
        viewModel.getTickets()
    }

    //Display each ticket
    LazyColumn {
        items(ticketsList) { ticket ->
            // Launch a coroutine to fetch the event for this ticket
            val event = remember { mutableStateOf<Event?>(null) }

            LaunchedEffect(ticket.concert_id) {
                event.value = viewModel.getEventData(ticket.concert_id)
            }

            event.value?.let {
                TicketComponent(event = it) {
                    // Button click behavior here, e.g. navigate to details
                    //Here make the QrCode screen route
                    Log.d("TicketsListScreen", "Button clicked")
                    val ticketcount = viewModel.getTicketCountPerCid(it.cid)
                    navController.navigate("ticketqrcode/${ticketcount}/${it.name}/${it.address}/${it.datetime}")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }


}