package com.example.projetsy43.repository

import com.example.projetsy43.model.Order
import com.google.firebase.database.*

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
                onComplete(false, exception.message) // Failure callback with error message
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
}
