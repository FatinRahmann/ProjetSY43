package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.viewmodel.EventDetailsViewModel
/**
 * Factory class to create instances of [EventDetailsViewModel] with required repositories.
 *
 * This factory is necessary because [EventDetailsViewModel] has constructor parameters,
 * and the default ViewModelProvider cannot instantiate it without them.
 *
 * @property eventRepo Repository managing event data.
 * @property ticketRepo Repository managing ticket data.
 * @property orderRepo Repository managing order data.
 */
class EventDetailsViewModelFactory (
    private val eventRepo: EventRepository,
    private val ticketRepo: TicketRepository,
    private val orderRepo: OrderRepository
) : ViewModelProvider.Factory
{
    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param T The type of ViewModel.
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of the requested ViewModel.
     * @throws IllegalArgumentException If the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailsViewModel(eventRepo, ticketRepo, orderRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}