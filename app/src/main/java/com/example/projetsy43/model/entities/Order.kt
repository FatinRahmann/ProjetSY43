package com.example.projetsy43.model.entities

/**
 * Represents an Order entity in the application.
 *
 * @property order_id Unique ID of the order.
 * @property customer_id ID of the customer who placed the order.
 * @property seller_id ID of the seller associated with the order.
 * @property event_id ID of the event for which the order was placed.
 * @property price Total price of the order.
 * @property amount Number of tickets purchased in the order.
 * @property datetime Date and time when the order was created, stored as a String.
 */
data class Order(
    var order_id : String = "",
    val customer_id : String = "",
    val seller_id : String = "",
    val event_id : String = "",
    val price : Double = 0.0,
    val amount : Int = 0, //Expecting logic to know amount of tickets sold
    val datetime : String = ""
)
