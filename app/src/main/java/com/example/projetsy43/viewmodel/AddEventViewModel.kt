package com.example.projetsy43.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.model.repository.EventRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class AddEventViewModel(
    private val repository: EventRepository
) : ViewModel() {
    var cid by mutableStateOf("") //This one have to find logic later
    var name by mutableStateOf("")
    var description by mutableStateOf("No description was added yet")
    var datetime by mutableStateOf<LocalDateTime?>(null)
    var address by mutableStateOf("")
    var sellerId by mutableStateOf("")
    var type by mutableStateOf("")
    var price by mutableStateOf(0.0)
    var capacity by mutableStateOf(0)
    var coverImage by mutableStateOf("defaultroute")
    var attraction by mutableStateOf("")

    fun onSubmitEvent(onSuccess: () -> Unit) {

        viewModelScope.launch {

            val user = UserSession.currentUser?: return@launch

            val newEvent = Event(
                name = name,
                description = description,
                datetime = datetime.toString(),
                address = address,
                seller_id = user.uid,
                type = type,
                price = price,
                capacity = capacity,
                avaliablecapacity = capacity,
                cover_image = coverImage,
                attraction = attraction
            )

            val result = repository.addOrUpdateEvent(newEvent)
            result.fold(
                onSuccess = {
                    // methode onSucces du bouton addEvent
                    onSuccess()
                },
                onFailure = { error ->
                }
            )
        }

    }

    fun updateEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        repository.updateEvent(
            eventId,
            name,
            description,
            address,
            type,
            coverImage,
            attraction,
            datetime,
            price,
            capacity,
            onSuccess,
            onError
        )
    }

}