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
     */
    suspend fun addOrUpdateOrder(order: Order) : Result<Unit> = suspendCancellableCoroutine { cont ->
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
     * @return List<Order> list with all orders
     */
    suspend fun getAllOrders() : List<Order> = suspendCancellableCoroutine { cont ->
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
     * @return Order? order object or null
     */
    //TODO: Manage case where no orders where found but not here! In the ViewModel
    suspend fun getOrderById(orderId: String) : Order? = suspendCancellableCoroutine { cont->
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
     */
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
     * @return List<Order> with the orders related to the seller, it can be empty
     */

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
