package com.example.projetsy43.model.repository

import com.example.projetsy43.model.entities.Order
import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
/**
 * Repository responsible for managing Order data in Firebase Realtime Database.
 *
 * Provides suspend functions to add, update, retrieve, and delete orders asynchronously.
 */
class OrderRepository {
    // Reference to the "orders" node in the database
    private val databaseReference = FirebaseDatabase.getInstance().getReference("orders")


    /**
     * Adds a new Order or updates an existing one in the database.
     * If the Order's order_id is empty, a new unique ID is generated.
     *
     * @param order The Order object to add or update.
     * @return Result<Unit> indicating success or failure of the operation.
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
     * @return List<Order> A list containing all orders, might be empty if no orders are found.
     * @throws Exception If the database operation is cancelled or fails.
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


    //TODO: Manage case where no orders where found but not here! In the ViewModel

    /**
     * Retrieves a single Order by its unique ID.
     *
     * @param orderId The unique ID of the Order to retrieve.
     * @return Order? The Order object if found, null otherwise.
     * @throws Exception If the database operation is cancelled or fails.
     */
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
     * @return Result<Unit> indicating whether the deletion was successful or not.
     * @throws Exception If the database operation is cancelled or fails.
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
     * Retrieves all Orders associated with a given seller's ID.
     *
     * @param sellerId The ID of the seller whose orders are to be retrieved.
     * @return List<Order> A list containing all orders associated with the given seller ID.
     * @throws Exception If the database operation is cancelled or fails.
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
