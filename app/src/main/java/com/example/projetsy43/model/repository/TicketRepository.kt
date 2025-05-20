package com.example.projetsy43.repository

import com.example.projetsy43.model.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TicketRepository {

    // Reference to the "tickets" node in Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("tickets")

    /**
     * Adds a new Ticket or updates an existing one.
     * If the Ticket ID (tid) is empty, generates a new unique ID automatically.
     *
     * @param ticket The Ticket object to add or update.
     * @param onComplete Callback invoked with success status and optional error message.
     */
    fun addOrUpdateTicket(ticket: Ticket, onComplete: (Boolean, String?) -> Unit) {
        if (ticket.tid.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                onComplete(false, "Failed to generate new ID")
                return
            }
            ticket.tid = newId
        }

        // Save the event under its ID in the database
        databaseReference.child(ticket.tid).setValue(ticket)
            .addOnSuccessListener {
                onComplete(true, null) // Success callback
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message) // Failure callback with error message
            }
    }

    suspend fun addOrUpdateTicketCoroutine(ticket: Ticket): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (ticket.tid.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                cont.resume(Result.failure(Exception("Failed to generate ticket ID")))
                return@suspendCancellableCoroutine
            }
            ticket.tid = newId
        }

        databaseReference.child(ticket.tid).setValue(ticket)
            .addOnSuccessListener {
                cont.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception))
            }
    }



    /**
     * Retrieves all Tickets from the database.
     *
     * @param onTicketsLoaded Callback invoked with the list of Tickets.
     * @param onError Callback invoked with an error message if retrieval fails.
     */
    fun getAllTickets(
        onTicketsLoaded: (List<Ticket>) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tickets = mutableListOf<Ticket>()
                for (child in snapshot.children) {
                    val ticket = child.getValue(Ticket::class.java)
                    ticket?.let { tickets.add(it) }
                }
                onTicketsLoaded(tickets)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    suspend fun getAllTicketsCoroutine(): List<Ticket> = suspendCancellableCoroutine { cont ->
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tickets = mutableListOf<Ticket>()
                for (child in snapshot.children) {
                    val ticket = child.getValue(Ticket::class.java)
                    ticket?.let { tickets.add(it) }
                }
                cont.resume(tickets)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(Exception(error.message))
            }
        })
    }

    /**
     * Retrieves all Tickets for a specific costumer_id.
     *
     * @param costumerId The concert ID to filter tickets by.
     * @param onTicketsLoaded Callback invoked with the filtered list of Tickets.
     * @param onError Callback invoked with an error message if retrieval fails.
     */
    fun getTicketsByCostumerId(
        costumerId: String,
        onTicketsLoaded: (List<Ticket>) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.orderByChild("costumer_id").equalTo(costumerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tickets = mutableListOf<Ticket>()
                    for (child in snapshot.children) {
                        val ticket = child.getValue(Ticket::class.java)
                        ticket?.let { tickets.add(it) }
                    }
                    onTicketsLoaded(tickets)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    suspend fun getTicketsByCostumerIdCoroutine(costumerId: String): List<Ticket> = suspendCancellableCoroutine { cont ->
        databaseReference.orderByChild("costumer_id").equalTo(costumerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tickets = mutableListOf<Ticket>()
                    for (child in snapshot.children) {
                        val ticket = child.getValue(Ticket::class.java)
                        ticket?.let { tickets.add(it) }
                    }
                    cont.resume(tickets)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(Exception(error.message))
                }
            })
    }

    /**
     * Deletes a Ticket by its ID.
     *
     * @param ticketId The unique ID of the Ticket to delete.
     * @param onComplete Callback invoked with success status and optional error message.
     */
    fun deleteTicket(ticketId: String, onComplete: (Boolean, String?) -> Unit) {
        databaseReference.child(ticketId).removeValue()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { ex -> onComplete(false, ex.message) }
    }

    suspend fun deleteTicketCoroutine(ticketId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        databaseReference.child(ticketId).removeValue()
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> cont.resume(Result.failure(exception)) }
    }

    /**
     * Retrieves a single Ticket by its ID.
     *
     * @param ticketId The unique ID of the Ticket to retrieve.
     * @param onTicketLoaded Callback invoked with the Ticket object or null if not found.
     * @param onError Callback invoked with an error message if retrieval fails.
     */
    fun getTicketById(
        ticketId: String,
        onTicketLoaded: (Ticket?) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child(ticketId).get()
            .addOnSuccessListener { snapshot ->
                val ticket = snapshot.getValue(Ticket::class.java)
                onTicketLoaded(ticket)
            }
            .addOnFailureListener { ex -> onError(ex.message ?: "Unknown error") }
    }


    suspend fun getTicketByIdCoroutine(ticketId: String): Ticket? = suspendCancellableCoroutine { cont ->
        databaseReference.child(ticketId).get()
            .addOnSuccessListener { snapshot ->
                val ticket = snapshot.getValue(Ticket::class.java)
                cont.resume(ticket)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(Exception(exception.message))
            }
    }


    //CALL EXAMPLE FOR THE delete:
    /*
    viewModelScope.launch {
    val result = repository.deleteEventCoroutine("someEventId")
    result
        .onSuccess {
            // Handle success
            println("Event deleted successfully")
        }
        .onFailure { error ->
            // Handle failure
            println("Error: ${error.message}")
        }
}
     */
}
