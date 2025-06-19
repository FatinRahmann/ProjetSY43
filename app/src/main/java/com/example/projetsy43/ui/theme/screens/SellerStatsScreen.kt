package com.example.projetsy43.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.TicketRepository

data class EventStat(
    val eventName: String,
    val ticketsSold: Int,
    val capacity: Int
)

@Composable
fun SellerStatsScreen(navController: NavHostController) {
    val sellerId = UserSession.currentUser?.uid ?: return
    val eventRepo = remember { EventRepository() }
    val ticketRepo = remember { TicketRepository() }

    var eventStats by remember { mutableStateOf<List<Triple<String, Int, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val allEvents = eventRepo.getEvents()
        val allTickets = ticketRepo.getAllTickets()

        val sellerEvents = allEvents.filter { it.seller_id == sellerId }
        val sellerEventMap = sellerEvents.associateBy { it.cid }

        val sellerTickets = allTickets.filter { ticket -> sellerEventMap.containsKey(ticket.concert_id) }

        val stats = sellerEvents.map { event ->
            val soldCount = sellerTickets.count { it.concert_id == event.cid }
            Triple(event.name, soldCount, event.capacity)
        }.sortedByDescending { it.second }

        eventStats = stats
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Tickets Sold",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            modifier = Modifier.padding(bottom = 16.dp)

        )

        eventStats.forEach { (eventName, sold, capacity) ->
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = cardElevation(6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = eventName,
                        fontSize = 18.sp,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
                    ) {
                        val soldRatio = if (capacity > 0) sold.toFloat() / capacity else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(soldRatio)
                                .fillMaxHeight()
                                .background(Color(0xFF7B1FA2), shape = RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sold: $sold", fontSize = 14.sp)
                        Text(text = "Tickets left: ${capacity - sold}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun EventStatsBarChart(eventStats: List<EventStat>) {
    eventStats.forEach { stat ->
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(stat.eventName, fontSize = 14.sp)

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val max = maxOf(stat.capacity, stat.ticketsSold, 1)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction = stat.ticketsSold / max.toFloat())
                        .background(Color(0xFF6A1B9A))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction = stat.capacity / max.toFloat())
                        .background(Color.LightGray)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sold: ${stat.ticketsSold}")
                Text("Cap: ${stat.capacity}")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
