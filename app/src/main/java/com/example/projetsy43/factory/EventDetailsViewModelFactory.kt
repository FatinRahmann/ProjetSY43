package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.viewmodel.EventDetailsViewModel

class EventDetailsViewModelFactory (
    private val eventRepo: EventRepository,
    private val ticketRepo: TicketRepository,
    private val orderRepo: OrderRepository
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailsViewModel(eventRepo, ticketRepo, orderRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}