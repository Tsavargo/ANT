package com.example.models

data class Connection(
    val fromStop: Stop,
    val toStop: Stop,
    val departure: Int,
    val arrival: Int,
    val trip: Trip)