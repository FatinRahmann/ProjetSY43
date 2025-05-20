package com.example.projetsy43.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.projetsy43.model.Event
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.repository.EventRepository

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

        repository.addOrUpdateEvent(newEvent) { success, errorMsg ->
            if (success) {
                // Handle success, e.g. notify UI, clear form, navigate, etc.
                println("Event saved successfully!")
            } else {
                // Handle error, e.g. show error message to user
                println("Failed to save event: $errorMsg")
            }
        }

    }
}