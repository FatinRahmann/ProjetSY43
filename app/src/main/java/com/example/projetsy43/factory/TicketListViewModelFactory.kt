package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.viewmodel.TicketListViewModel
/**
 * Factory class to create instances of [TicketListViewModel] with required repositories.
 *
 * This factory is necessary because [TicketListViewModel] has constructor parameters
 * and cannot be instantiated directly by the default ViewModelProvider.
 *
 * @property ticketRepo Repository used to access ticket-related data.
 * @property eventRepo Repository used to access event-related data.
 */
class TicketListViewModelFactory (
    private val ticketRepo: TicketRepository,
    private val eventRepo: EventRepository
) : ViewModelProvider.Factory
{
    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of the requested ViewModel.
     * @throws IllegalArgumentException If the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketListViewModel(ticketRepo, eventRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}