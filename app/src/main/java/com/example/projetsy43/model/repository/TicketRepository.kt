package com.example.projetsy43.model.repository

import com.example.projetsy43.model.entities.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
/**
 * Repository responsible for managing Ticket data in Firebase Realtime Database.
 *
 * Provides suspend functions to add, update, retrieve, and delete tickets asynchronously.
 */
class TicketRepository {

    // Reference to the "tickets" node in Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("tickets")


    /**
     * Adds a new Ticket or updates an existing one in the database.
     * If the Ticket's ID is empty, a new unique ID is generated.
     *
     * @param ticket The Ticket object to add or update.
     * @return Result<Unit> indicating success or failure of the operation.
     */
    suspend fun addOrUpdateTicket(ticket: Ticket): Result<Unit> = suspendCancellableCoroutine { cont ->
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
     * @return List<Ticket> A list containing all tickets, might be empty if no tickets are found.
     * @throws Exception If the database operation is cancelled or fails.
     */
    suspend fun getAllTickets(): List<Ticket> = suspendCancellableCoroutine { cont ->
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
     * Retrieves all Tickets associated with a specific customer's ID.
     *
     * @param costumerId The ID of the customer whose tickets are to be retrieved.
     * @return List<Ticket> A list containing all tickets associated with the given customer ID.
     * @throws Exception If the database operation is cancelled or fails.
     */
    suspend fun getTicketsByCostumerId(costumerId: String): List<Ticket> = suspendCancellableCoroutine { cont ->
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
     * Deletes a Ticket by its unique ID.
     *
     * @param ticketId The unique ID of the Ticket to delete.
     * @return Result<Unit> indicating whether the deletion was successful or not.
     * @throws Exception If the database operation is cancelled or fails.
     */
    suspend fun deleteTicket(ticketId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        databaseReference.child(ticketId).removeValue()
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { exception -> cont.resume(Result.failure(exception)) }
    }


    /**
     * Retrieves a single Ticket by its unique ID.
     *
     * @param ticketId The unique ID of the Ticket to retrieve.
     * @return Ticket? The Ticket object if found, null otherwise.
     * @throws Exception If the database operation is cancelled or fails.
     */
    suspend fun getTicketById(ticketId: String): Ticket? = suspendCancellableCoroutine { cont ->
        databaseReference.child(ticketId).get()
            .addOnSuccessListener { snapshot ->
                val ticket = snapshot.getValue(Ticket::class.java)
                cont.resume(ticket)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(Exception(exception.message))
            }
    }

}
