package com.example.projetsy43.model.entities


data class Ticket(
    var tid: String = "",
    val concert_id: String = "",
    val costumer_id: String = "",
    //Might add fields like sit, date, etc... When needed!
    val qrcode : String = ""
)