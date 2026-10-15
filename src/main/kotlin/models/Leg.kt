package com.example.models

class Leg(
    val trip: Trip,
    val fromStop: Stop,
    val toStop: Stop,
    val departureTime: Int,
    val arrivalTime: Int)