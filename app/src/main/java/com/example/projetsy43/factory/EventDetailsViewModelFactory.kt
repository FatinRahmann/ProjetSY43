package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.repository.EventRepository
import com.example.projetsy43.viewmodel.EventDetailsViewModel

class EventDetailsViewModelFactory (
    private val repository: EventRepository
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}