package com.example.projetsy43.viewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.entities.Event
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: EventRepository
) : ViewModel()
{
    var searchQuery by mutableStateOf("")
    var allEvents by mutableStateOf<List<Event>>(emptyList())  // liste des events charges depuis Firebase
    //Filtering events
    val filteredEvents: List<Event>
        get() = allEvents.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.attraction.contains(searchQuery, ignoreCase = true) || it.address.contains(searchQuery, ignoreCase = true)
        }

    //For the event selection
    var selectedEvent: Event? by mutableStateOf(null)

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                val events = repository.getEvents()
                allEvents = events
                Log.d("HomeViewModel", "Fetched events: ${events.size}")
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error")
            }
        }
    }
}