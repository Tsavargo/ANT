package com.example.models.routing

import com.example.models.data.Stop
import com.example.models.data.Trip

data class Leg(
    val trip: Trip,
    val fromStop: Stop,
    val toStop: Stop,
    val departureTime: Int,
    val arrivalTime: Int
)