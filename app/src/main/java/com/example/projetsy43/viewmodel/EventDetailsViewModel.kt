package com.example.projetsy43.viewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.entities.Event
import com.example.projetsy43.model.entities.Order
import com.example.projetsy43.model.entities.Ticket
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now


//TODO: In all viewModel evaluate the need of setting some variables to private

// ViewModel that manages state and logic for the EventDetailScreen
class EventDetailsViewModel(
    private val eventRepo: EventRepository,
    private val ticketRepo: TicketRepository,
    private val orderRepo: OrderRepository

) : ViewModel() {

    var eventState = mutableStateOf<Event?>(null) // Holds the event details
    var price by mutableDoubleStateOf(0.0) // Total cost based on ticket count
    var amount by mutableIntStateOf(0) // Number of ticket user wants to buy
    var avaliablecapacity by mutableIntStateOf(0) // Remaining tickets
    var alertMessage by mutableStateOf<String?>(null) // For toast alerts
        private set

    fun clearAlert() {
        alertMessage = null
    }

    // Returns true and sets a toast if user exceeds ticket limits
    fun setAlert() : Boolean {
        if (amount >= 4) {
            showToast ("One user can only buy a maximum of 4 tickets!")
            return true
        }
        if (amount >= avaliablecapacity) {
            showToast("You have selected all tickets!")
            return true
        }
        return false
    }

    // Toast Logic
    var isToastVisible by mutableStateOf(false)
        private set

    fun showToast(message: String) {
        alertMessage = message
        isToastVisible = true
    }

    fun dismissToast() {
        isToastVisible = false
        alertMessage = null
    }

    // Loads event details by ID, and initializes price, amount, and capacity
    fun loadEventById(eventId : String) {

        viewModelScope.launch {
            try {
                // Handle if id == 0000
                if(eventId == "0000"){
                    showToast("Event Not Found")
                    return@launch
                }

                val result = eventRepo.getEventById(eventId)

                if (result == null){
                    showToast("No event found with this ID")
                    return@launch
                }

                eventState.value = result
                eventState.value?.let { e ->
                    price = e.price
                    amount = 1
                    avaliablecapacity = e.avaliablecapacity
                }
            } catch (e : Exception) {
                Log.e("EventDetailsViewModel" , "Error loading event: ${e.message}")
                showToast("Error loading event . Please try again")
            }
        }
    }

    // Handle Ticket + Order Purchase Logic
    fun buyTicket() {
        viewModelScope.launch {
            try {
                // Validate that both event and user are available
                var event = eventState.value ?: return@launch
                val user = UserSession.currentUser ?: return@launch
                //val ticketamount = (price / event.price).toInt()


                // Check ticket capacity
                if (amount > avaliablecapacity) {
                    showToast("Not enough tickets available.")
                    Log.d("EventDetailsViewModel", "Not enough capacity")
                    return@launch
                }

                // Add tickets one by one to database
                for (t in 1..amount) {
                    val newTicket = Ticket(
                        concert_id = event.cid,
                        costumer_id = user.uid
                    )

                    val resultTicket = ticketRepo.addOrUpdateTicket(newTicket)

                    if (resultTicket.isFailure) {
                        Log.d(
                            "HomeViewModel",
                            "Failed to add ticket: ${resultTicket.exceptionOrNull()?.message}"
                        )
                        return@launch
                    }

                    Log.d("HomeViewModel", "Ticket successfully added")
                }

                // Reduce available capacity after purchase
                avaliablecapacity -= amount

                // Create and insert the order
                val newOrder = Order(
                    customer_id = user.uid,
                    seller_id = event.seller_id,
                    event_id = event.cid,
                    price = price,
                    amount = amount,
                    datetime = LocalDateTime.now().toString()
                )

                val resultOrder = orderRepo.addOrUpdateOrder(newOrder)

                if (resultOrder.isFailure) {
                    Log.d(
                        "HomeViewModel",
                        "Failed to add order: ${resultOrder.exceptionOrNull()?.message}"
                    )
                    return@launch
                }

                Log.d("HomeViewModel", "Order successfully added")

                // Update event's available capacity in the firebase
                event.avaliablecapacity = avaliablecapacity

                val eventupdate = eventRepo.addOrUpdateEvent(event)

                eventupdate.onSuccess {
                    Log.d("HomeViewModel", "Event succesfully updateed")
                }

                eventupdate.onFailure {
                    Log.d("HomeViewModel", "Event failed to be updated")
                }


            } catch (e: Exception) {
                Log.e("EventDetailsViewModel", "Unexpected error during purchase: ${e.message}")
                showToast("Something went wrong during ticket purchase.")
            }
        }
    }
    // Update price after increment/decrement
    fun updatePrice() {
        eventState.value?.let {
            price = amount * it.price
            Log.d("EventDetailsViewModel", "Price now is: ${price}}")
        }

    }

    // Increment ticket count (max 4 and within capacity)
    fun increment() {
        if (amount >= 4 || amount + 1 > avaliablecapacity) {
            showToast("Maximum 4 tickets per user.")
            Log.d("EventDetailsViewModel", "Either > 4 either amout > avaliablecapacity")
            return
        }
        amount++
        Log.d("EventDetailsViewModel", "Amount after increment now is: ${amount}}")
        updatePrice()
    }

    // Decrement ticket count (minimum 1)
    fun decrement() {
        if (amount <= 1) {
            showToast("Min Purchase ticket : 1")
            return
        }
        amount--
        Log.d("EventDetailsViewModel", "Amount after decrement now is: ${amount}}")
        updatePrice()
    }

    // Shows total price or "Sold Out" if no more tickets
    fun getPurchaseButtonTest() : String {
        if(avaliablecapacity == 0) {
            return "This event was sold out!"
        } else {
            return price.toString()
        }
    }

    // Shared Viewmodel for payment
    fun prepareForPayment(eventId: String, amt: Int, totalPrice: Float) {
        eventState.value?.cid?.let {
            if (it == eventId) {
                amount = amt
                price = totalPrice.toDouble()
            } else {
                // Event mismatch: load new event
                loadEventById(eventId)
                amount = amt
                price = totalPrice.toDouble()
            }
        } ?: run {
            // No event loaded yet
            loadEventById(eventId)
            amount = amt
            price = totalPrice.toDouble()
        }
    }
    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val event = eventRepo.getEventById(eventId)

            if (event?.seller_id != UserSession.currentUser?.uid) {
                alertMessage = "You are not authorized to delete this event"
                showToast(alertMessage!!)
                return@launch
            }

            val result = eventRepo.deleteEvent(eventId)
            if (result.isSuccess) {
                onSuccess()
            } else {
                alertMessage = "Failed to delete event"
                showToast(alertMessage!!)
            }
        }
    }
}