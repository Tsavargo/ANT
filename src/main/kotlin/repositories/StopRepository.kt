package com.example.repositories

import com.example.models.data.Stop

class StopRepository {
    private val stops = mutableMapOf<String, Stop>()

    fun add(stop: Stop) {
        stops[stop.stopId] = stop
    }

    fun get(stopId: String): Stop = stops[stopId] ?: error("Unknown Stop: $stopId")

    fun all(): Collection<Stop> = stops.values
}