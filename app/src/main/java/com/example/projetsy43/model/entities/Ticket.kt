package com.example.projetsy43.model.entities


/**
 * Represents a Ticket entity in the application.
 *
 * @property tid Unique ID of the ticket.
 * @property concert_id ID of the concert or event associated with the ticket.
 * @property costumer_id ID of the customer who owns the ticket.
 * @property qrcode QR code string representing the ticket, used for scanning and verification.
 */
data class Ticket(
    var tid: String = "",
    val concert_id: String = "",
    val costumer_id: String = "",
    //Might add fields like sit, date, etc... When needed!
    val qrcode : String = ""
)