package com.example.models.routing

import com.example.models.data.Stop
import com.example.models.data.Trip

data class Connection(
    val fromStop: Stop,
    val toStop: Stop,
    val departure: Int,
    val arrival: Int,
    val trip: Trip
)