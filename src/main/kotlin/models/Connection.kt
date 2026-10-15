package com.example.models

class Connection(
    val fromStop: Stop,
    val toStop: Stop,
    val departure: Int,
    val arrival: Int,
    val trip: Trip)