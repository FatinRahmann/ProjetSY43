package com.example.projetsy43.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.model.repository.EventRepository
import kotlinx.coroutines.launch

class AddEventViewModel(
    private val repository: EventRepository
) : ViewModel() {
    var cid by mutableStateOf("") //This one have to find logic later
    var name by mutableStateOf("")
    var description by mutableStateOf("No description was added yet")
    var datetime by mutableStateOf("") // We can later use a better type for date/time
    var address by mutableStateOf("")
    var sellerId by mutableStateOf("")
    var type by mutableStateOf("")
    var price by mutableStateOf(0.0)
    var capacity by mutableStateOf(0)
    var availableCapacity by mutableStateOf(0)
    var coverImage by mutableStateOf("defaultroute")
    var attraction by mutableStateOf("")

    fun onSubmitEvent() {
        val newEvent = Event(
            name = name,
            description = description,
            datetime = "0000-00-00 00:00:00",
            address = address,
            seller_id = "100",
            type = type,
            price = price,
            capacity = capacity,
            avaliablecapacity = 100,
            cover_image = coverImage,
            attraction = attraction
        )

        viewModelScope.launch {
            val result = repository.addOrUpdateEvent(newEvent)
            result.fold(
                onSuccess = {
                    //TODO: do something here
                },
                onFailure = { error ->
                    //TODO:do something here
                }
            )
        }

    }
}