package com.example.sirasakandroid

data class houses(
    val id: Int,
    val AreaSize: Double,
    val Bedrooms: Int,
    val Bathrooms: Int,
    val Price: Double,
    val Condition: String, // Fixed typo from "Conditionn" to "Condition"
    val HouseType: String,
    val YearBuilt: Int,
    val ParkingSpaces: Int,
    val Address: String,
    val HouseImage: String // Optional property for the image URL
)
