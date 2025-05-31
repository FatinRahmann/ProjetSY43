package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.viewmodel.TicketListViewModel

class TicketListViewModelFactory (
    private val ticketRepo: TicketRepository,
    private val eventRepo: EventRepository
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketListViewModel(ticketRepo, eventRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}