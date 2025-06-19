package com.example.projetsy43.model.repository
import com.example.projetsy43.model.entities.Event
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
/**
 * Repository responsible for managing Event data in Firebase Realtime Database.
 *
 * Provides suspend functions to add, update, retrieve, and delete events asynchronously.
 */
class EventRepository {

    // Reference to the "events" node in Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("testevents")

    /**
     * Adds a new Event or updates an existing one in the database.
     * If the Event's ID is empty, a new unique ID is generated.
     *
     * @param event The Event object to add or update.
     * @return Result<Unit> indicating success or failure of the operation.
     * TODO: Handle adding an image to the drawable resources.
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

        // Write event to database
        databaseReference.child(event.cid).setValue(event)
            .addOnSuccessListener {
                cont.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception))
            }
    }


    /**
     * Retrieves all Events from the database.
     *
     * @return List<Event> list with all events
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
     * Retrieves a single Event by its unique ID.
     *
     * @param eventId The unique ID of the Event to retrieve.
     * @return Event? event object or null
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
     * Deletes an Event by its unique ID.
     *
     * @param eventId The unique ID of the Event to delete.
     * @return Result<Unit> indicating whether the deletion was successful or not.
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

    //TODO: Faire function pour pouvoir recuperer evenements liees a un seller


}