package com.example.models.data

data class StopTime(
    val tripId: String,
    val stopId: String,
    val sequence: Int,
    val arrival: Int,
    val departure: Int
)
