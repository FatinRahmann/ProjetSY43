package com.example.projetsy43.model.repository

import com.example.projetsy43.model.entities.Order
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OrderRepository {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("orders")

    /**
     * Adds a new Order or updates an existing one in the database.
     *
     * @param order The Order object to be added or updated.
     * @param onComplete A callback function with a Boolean indicating success and an optional error message.
     */
    fun addOrUpdateOrder(order: Order, onComplete: (Boolean, String?) -> Unit) {
        // If no ID is set, generate a new key from Firebase push()
        if (order.order_id.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                onComplete(false, "Failed to generate new ID")
                return
            }
            order.order_id = newId
        }

        // Save the event under its ID in the database
        databaseReference.child(order.order_id).setValue(order)
            .addOnSuccessListener {
                onComplete(true, null) // Success callback
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message) // Failure
            }
    }

    suspend fun addOrUpdateOrderCoroutine(order: Order) : Result<Unit> = suspendCancellableCoroutine { cont ->
        if (order.order_id.isEmpty()) {
            val newId = databaseReference.push().key
            if (newId == null) {
                cont.resume(Result.failure(Exception("Failed to generate order ID")))
                return@suspendCancellableCoroutine
            }
            order.order_id = newId
        }

        // Save the event under its ID in the database
        databaseReference.child(order.order_id).setValue(order)
            .addOnSuccessListener {
                cont.resume(Result.success(Unit)) // Success
            }
            .addOnFailureListener { exception ->
                cont.resume(Result.failure(exception)) // Failure
            }
    }


    /**
     * Retrieves all Orders from the database.
     *
     * @param onOrdersLoaded Callback function that returns a list of Order objects.
     * @param onError Callback function that returns an error message in case of failure.
     */
    fun getAllOrders(
        onOrdersLoaded: (List<Order>) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    order?.let { orders.add(it) }
                }
                onOrdersLoaded(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    suspend fun getAllOrdersCoroutine() : List<Order> = suspendCancellableCoroutine { cont ->
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    order?.let { orders.add(it) }
                }
                cont.resume(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(Exception(error.message))
            }
        })
    }

    /**
     * Retrieves a single Order by its unique ID.
     *
     * @param orderId The unique ID of the Order to retrieve.
     * @param onOrderLoaded Callback function that returns the Order object or null if not found.
     * @param onError Callback function that returns an error message in case of failure.
     */
    fun getOrderById(
        orderId: String,
        onOrderLoaded: (Order?) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.child(orderId).get()
            .addOnSuccessListener { snapshot ->
                val order = snapshot.getValue(Order::class.java)
                onOrderLoaded(order)
            }
            .addOnFailureListener { ex ->
                onError(ex.message ?: "Unknown error")
            }
    }

    //TODO: Manage case where no orders where found but not here! In the ViewModel
    suspend fun getOrderByIdCoroutine(orderId: String) : Order? = suspendCancellableCoroutine { cont->
        databaseReference.child(orderId).get()
            .addOnSuccessListener { snapshot ->
                val order = snapshot.getValue(Order::class.java)
                cont.resume(order)
            }
            .addOnFailureListener { ex ->
                cont.resumeWithException(Exception(ex.message))
            }
    }

    /**
     * Deletes an Order by its unique ID.
     *
     * @param orderId The unique ID of the Order to delete.
     * @param onComplete Callback function that returns a Boolean indicating success and an optional error message.
     */
    fun deleteOrder(orderId: String, onComplete: (Boolean, String?) -> Unit) {
        databaseReference.child(orderId).removeValue()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { ex -> onComplete(false, ex.message) }
    }


    suspend fun deleteOrder(orderId: String) : Result<Unit> = suspendCancellableCoroutine { cont ->
        databaseReference.child(orderId).removeValue()
            .addOnSuccessListener {
                cont.resume(Result.success(Unit))
            }
            .addOnFailureListener { ex ->
                cont.resume(Result.failure(Exception(ex)))
            }
    }

    /**
     * Retrieves all Orders for a specific seller.
     *
     * @param sellerId The seller ID to filter Orders by.
     * @param callback Callback function that returns a list of matching Orders or an error message.
     */
    fun getOrdersBySellerId(
        sellerId: String,
        onOrdersLoaded: (List<Order>) -> Unit,
        onError: (String) -> Unit
    ) {
        databaseReference.orderByChild("seller_id").equalTo(sellerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()
                    for (child in snapshot.children) {
                        val order = child.getValue(Order::class.java)
                        order?.let { orders.add(it) }
                    }
                    onOrdersLoaded(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    suspend fun getOrdersBySellerId(sellerId: String) : List<Order> = suspendCancellableCoroutine { cont ->
        databaseReference.orderByChild("seller_id").equalTo(sellerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()
                    for (child in snapshot.children) {
                        val order = child.getValue(Order::class.java)
                        order?.let { orders.add(it) }
                    }
                    cont.resume(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(Exception(error.message))
                }
            })
    }
}
