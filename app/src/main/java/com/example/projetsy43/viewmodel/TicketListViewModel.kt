package com.example.projetsy43.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.model.entities.Ticket
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.TicketRepository
import kotlinx.coroutines.launch

class TicketListViewModel(
    private val ticketRepo : TicketRepository,
    private val eventRepo: EventRepository
) : ViewModel() {

    //All tickets
    val fetchedTickets = mutableStateListOf<Ticket>()
    //Ensure only one event is seen, even if there are multiple tickets
    var toShow = mutableStateListOf<Ticket>()


    fun getTickets() {
        viewModelScope.launch {
            val user = UserSession.currentUser?: return@launch

            try {
                val result = ticketRepo.getTicketsByCostumerId(user.uid)
                fetchedTickets.clear()
                fetchedTickets.addAll(result)
                val filteredTickets = fetchedTickets.distinctBy { it -> it.concert_id }
                toShow.addAll(filteredTickets)


            } catch (e: Exception) {
                Log.d("TicketListViewModel", "Error when geting user tickets: ${e.message}")
            }
        }
    }

    suspend fun getEventData(concert_id: String) : Event? {
        return eventRepo.getEventById(concert_id)
    }

    fun getTicketCountPerCid(cid: String) : Int {
        return fetchedTickets.count { it.concert_id == cid }
    }
}