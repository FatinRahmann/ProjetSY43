package com.example.projetsy43.factory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.viewmodel.AddEventViewModel
/**
 * Factory class responsible for creating an instance of [AddEventViewModel].
 * This is useful when a ViewModel requires constructor parameters.
 *
 * @property repository The repository instance used to access event data.
 */
class AddEventViewModelFactory (
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
        if (modelClass.isAssignableFrom(AddEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}