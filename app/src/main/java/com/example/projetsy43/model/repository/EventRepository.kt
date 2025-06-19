package com.example.projetsy43.model.repository
import com.example.projetsy43.model.entities.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EventRepository {

    // Reference to the "events" node in Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("testevents")

    /**
     * Adds a new Event or updates an existing one.
     * If the Event ID is empty, generates a new unique ID automatically.
     * @param event The Event object to add or update.
     * @param onComplete Callback for completion status (success or failure).
     * TODO: Handle adding an image to the drawable ressources
     */
    suspend fun addOrUpdateEvent(event: Event) : Result<Unit> = suspendCancellableCoroutine { cont ->
        // If no ID is set, generate a new key from Firebase push()
        if (event.cid.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                cont.resume(Result.failure(Exception("Failed to generate event ID")))
                return@suspendCancellableCoroutine
            }
            event.cid = newId
        }

        // Save the event under its ID in the database
        databaseReference.child(event.cid).setValue(event)
            .addOnSuccessListener {
                cont.resume(Result.success(Unit)) // Success
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception)) // Failure
            }
    }

    /**
     * Retrieves all Events from the database.
     * Uses a ValueEventListener to listen to data changes.
     * @return List<Event> With all events
     */
    suspend fun getEvents(): List<Event> = suspendCancellableCoroutine { cont ->
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                for (child in snapshot.children) {
                    var event = child.getValue(Event::class.java)
                    event?.let { events.add(it) }
                }
                cont.resume(events)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(Exception(error.message))
            }

        })
    }

    /**
     * Deletes an Event by its ID.
     * @param eventId The unique ID of the Event to delete.
     * @return Event? the object of the Event or null
     */
    suspend fun getEventById(eventId: String) : Event? = suspendCancellableCoroutine { cont ->
        databaseReference.child(eventId).get()
            .addOnSuccessListener { snapshot ->
                val event = snapshot.getValue(Event::class.java)
                cont.resume(event)
            }
            .addOnFailureListener { exception ->
                cont.resumeWithException(Exception(exception.message))
            }

    }


    /**
     * Deletes an Event by its ID.
     * @param eventId The unique ID of the Event to delete.
     */
    suspend fun deleteEvent(eventId : String) : Result<Unit> = suspendCancellableCoroutine { cont ->
        databaseReference.child(eventId).removeValue()
            .addOnSuccessListener {
                cont.resume(Result.success(Unit)) // Successfully deleted
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception)) // Deletion failed with error
            }
    }


    suspend fun getEventsBySellerId(sellerId: String): List<Event> = suspendCancellableCoroutine { cont ->
        databaseReference.orderByChild("seller_id").equalTo(sellerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val events = mutableListOf<Event>()
                    for (child in snapshot.children) {
                        val event = child.getValue(Event::class.java)
                        event?.let { events.add(it) }
                    }
                    cont.resume(events)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(Exception(error.message))
                }
            })
    }





}