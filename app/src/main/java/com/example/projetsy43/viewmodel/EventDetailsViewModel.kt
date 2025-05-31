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
class EventDetailsViewModel(
    private val eventRepo: EventRepository,
    private val ticketRepo: TicketRepository,
    private val orderRepo: OrderRepository

) : ViewModel() {

    var eventState = mutableStateOf<Event?>(null)
    var price by mutableDoubleStateOf(0.0)
    var amount by mutableIntStateOf(0)
    var avaliablecapacity by mutableIntStateOf(0)
    var alertMessage by mutableStateOf<String?>(null)
        private set

    fun clearAlert() {
        alertMessage = null
    }

    fun setAlert() : Boolean {
        if (amount >= 4) {
            alertMessage = "An user can only buy a maximum of 4 tickets!"
            return true
        }
        if (amount >= avaliablecapacity) {
            alertMessage = "You have selected all tickets!"
            return true
        }
        return false
    }

    fun loadEventById(eventId : String) {
        //TODO: Handle here, in case eventId = 0000 do something to show event not found or wathever, SEE ConcertNavGraph
        viewModelScope.launch {
            try {
                val result = eventRepo.getEventById(eventId)
                eventState.value = result
                eventState.value?.let { e ->
                    price = e.price
                    amount = 1
                    avaliablecapacity = e.avaliablecapacity
                }
            } catch (e : Exception) {
                //Handle error here
            }
        }
    }

    fun buyTicket() {
        viewModelScope.launch {
            try {
                //TODO: Handle an error here such as printing a message if theres not event value etc...
                var event = eventState.value ?: return@launch
                val user = UserSession.currentUser ?: return@launch
                //val ticketamount = (price / event.price).toInt()

                if (amount > avaliablecapacity) {
                    //TODO: add an alert here as there arent enough
                    //TODO: Maybe pass this logic inside of the increment button
                    Log.d("EventDetailsViewModel", "Not enough capacity")
                    return@launch
                }


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

                avaliablecapacity -= amount

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

                event.avaliablecapacity = avaliablecapacity

                val eventupdate = eventRepo.addOrUpdateEvent(event)

                eventupdate.onSuccess {
                    Log.d("HomeViewModel", "Event succesfully updateed")
                }

                eventupdate.onFailure {
                    Log.d("HomeViewModel", "Event failed to be updated")
                }


            } catch (e: Exception) {
                //TODO: Handle here
            }
        }
    }

    fun updatePrice() {
        eventState.value?.let {
            price = amount * it.price
            Log.d("EventDetailsViewModel", "Price now is: ${price}}")
        }

    }

    fun increment() {
        if (amount >= 4 || amount + 1 > avaliablecapacity) {
            Log.d("EventDetailsViewModel", "Either > 4 either amout > avaliablecapacity")
            return
        }
        amount++
        Log.d("EventDetailsViewModel", "Amount after increment now is: ${amount}}")
        updatePrice()
    }

    fun decrement() {
        if (amount <= 1) {
            //Handle here
            return
        }
        amount--
        Log.d("EventDetailsViewModel", "Amount after decrement now is: ${amount}}")
        updatePrice()
    }

    fun getPurchaseButtonTest() : String {
        if(avaliablecapacity == 0) {
            return "This event was sold out!"
        } else {
            return price.toString()
        }
    }


}