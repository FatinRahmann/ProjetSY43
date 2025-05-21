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
     */
    fun addOrUpdateEvent(event: Event, onComplete: (Boolean, String?) -> Unit) {
        // If no ID is set, generate a new key from Firebase push()
        if (event.cid.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                onComplete(false, "Failed to generate new ID")
                return
            }
            event.cid = newId
        }

        // Save the event under its ID in the database
        databaseReference.child(event.cid).setValue(event)
            .addOnSuccessListener {
                onComplete(true, null) // Success callback
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message) // Failure callback with error message
            }
    }

    suspend fun addOrUpdateEventCoroutine(event: Event) : Result<Unit> = suspendCancellableCoroutine { cont ->
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
     * Retrieves all Events from the database using coroutine
     *
     */

    suspend fun getEventsCoroutine(): List<Event> = suspendCancellableCoroutine { cont ->
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

    suspend fun getEventByIdCoroutine(eventId: String) : Event? = suspendCancellableCoroutine { cont ->
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
     * Retrieves all Events from the database.
     * Uses a ValueEventListener to listen to data changes.
     * @param onEventsLoaded Callback to receive the list of events.
     * @param onError Callback to receive any error messages. TODO: Watch here what the addValueEventListener will do since it will trigger the callback function every time something new is added
     */
    fun getEvents(
        onEventsLoaded: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val events = mutableListOf<Event>()
                // Iterate through all children in the "events" node
                for (childSnapshot in snapshot.children) {
                    // Deserialize each child into an Event object
                    val event = childSnapshot.getValue(Event::class.java)
                    event?.let { events.add(it) }
                }
                // Return the list of events through the callback
                onEventsLoaded(events)
            }

            override fun onCancelled(error: DatabaseError) {
                // Return the error message through the callback
                //TODO: decide what do with error
                onError(error.message)
            }
        })
    }

    suspend fun deleteEventCoroutine(eventId : String) : Result<Unit> = suspendCancellableCoroutine { cont ->
        databaseReference.child(eventId).removeValue()
            .addOnSuccessListener {
                cont.resume(Result.success(Unit)) // Successfully deleted
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception)) // Deletion failed with error
            }
    }


    /**
     * Deletes an Event by its ID.
     * @param eventId The unique ID of the Event to delete.
     * @param onComplete Callback to notify if the deletion was successful or failed.
     */
    fun deleteEvent(eventId: String, onComplete: (Boolean, String?) -> Unit) {
        databaseReference.child(eventId).removeValue()
            .addOnSuccessListener {
                onComplete(true, null) // Successfully deleted
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message) // Deletion failed with error
            }
    }

    /**
     * Retrieves a single Event by its ID.
     * @param eventId The unique ID of the Event to retrieve.
     * @param onEventLoaded Callback to receive the Event or null if not found.
     * @param onError Callback to receive any error messages.
     */
    fun getEventById(
        eventId: String,
        onEventLoaded: (Event?) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child(eventId).get()
            .addOnSuccessListener { snapshot ->
                val event = snapshot.getValue(Event::class.java)
                onEventLoaded(event)
            }
            .addOnFailureListener { exception ->
                onError(exception.message ?: "Unknown error")
            }
    }
}