package com.example.projetsy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetsy43.R

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

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Back Icon aligned top start
                Icon(
                    painter = painterResource(id = R.drawable.ic_retour),
                    contentDescription = "return",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = Color.Black
                )

                // Centered Title Text
                Text(
                    text = "My tickets",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
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
    }


}