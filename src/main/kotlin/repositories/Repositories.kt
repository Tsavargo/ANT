package com.example.repositories

/** The repositories the pipeline steps fill up. */
class Repositories {
    val stops = StopRepository()
    val routes = RouteRepository()
    val trips = TripRepository()
    val stopTimes = StopTimeRepository()
}