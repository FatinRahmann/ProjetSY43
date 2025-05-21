package com.example.projetsy43.model.entities

//Datetime either store as Long either as String

data class Order(
    var order_id : String = "",
    val customer_id : String = "",
    val seller_id : String = "",
    val event_id : String = "",
    val price : Double = 0.0,
    val amount : Int = 0, //Expecting logic to know amount of tickets sold
    val datetime : String = ""
)
