package com.example.projetsy43.repository

import com.example.projetsy43.model.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
}
