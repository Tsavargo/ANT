package com.example.repositories

import com.example.models.data.Trip

class TripRepository {
    private val trips = mutableMapOf<String, Trip>()

    fun add(trip: Trip) {
        trips[trip.tripId] = trip
    }

    fun get(tripId: String): Trip = trips[tripId] ?: error("Unknown Trip: $tripId")

    fun all(): Collection<Trip> = trips.values
}