package com.example.projetsy43.model

//Datetime either store as Long either as String

data class Event(
    val cid : String = "",
    val name : String = "",
    val description : String = "No description was added yet",
    val datetime: String = "", //Here might have to change the type
    val address: String = "",
    val seller_id : String = "",
    val type : String = "",
    val price: Double = 0.0,
    val capacity: Int = 0,
    val avaliablecapacity : Int = 0,
    val cover_image : String = "defaultroute", //Here add the default route!
    val attraction: String = "", //Find a way here to represent many artists: First idea, artists separated by coma
)
