package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.viewmodel.HomeViewModel
/**
 * Factory class to create instances of [HomeViewModel] with a required [EventRepository].
 *
 * This factory is needed because [HomeViewModel] requires constructor parameters
 * and the default ViewModelProvider cannot instantiate it directly.
 *
 * @property repository Repository instance used by the HomeViewModel to fetch event data.
 */
class HomeViewModelFactory (
    private val repository: EventRepository
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
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}