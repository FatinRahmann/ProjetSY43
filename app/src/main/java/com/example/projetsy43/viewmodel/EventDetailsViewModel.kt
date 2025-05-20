package com.example.projetsy43.viewmodel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.Event
import com.example.projetsy43.repository.EventRepository
import kotlinx.coroutines.launch
//TODO: In all viewModel evaluate the need of setting some variables to private
class EventDetailsViewModel(
    private val repository: EventRepository
) : ViewModel() {

    var eventState = mutableStateOf<Event?>(null)

    fun loadEventById(eventId : String) {
        //TODO: Handle here, in case eventId = 0000 do something to show event not found or wathever, SEE ConcertNavGraph
        viewModelScope.launch {
            try {
                val result = repository.getEventByIdCoroutine(eventId)
                eventState.value = result
            } catch (e : Exception) {
                //Handle error here
            }
        }
    }

}