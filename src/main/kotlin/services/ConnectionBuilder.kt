package com.example.services

import com.example.models.data.StopTime
import com.example.models.routing.Connection
import com.example.repositories.Repositories

object ConnectionBuilder {
    fun build(repositories: Repositories): List<Connection> {
        val connections = mutableListOf<Connection>()
        for (trip in repositories.trips.all()) {
            val stopTimes = repositories.stopTimes.forTrip(trip.tripId)

            var previous: StopTime? = null
            for (current in stopTimes) {
                if (previous != null) {
                    val connection = Connection(
                        fromStop = repositories.stops.get(previous.stopId),
                        toStop = repositories.stops.get(current.stopId),
                        departure = previous.departure,
                        arrival = current.arrival,
                        trip = trip
                    )
                    connections.add(connection)
                }
                previous = current
            }
        }

        connections.sortBy { it.departure }
        return connections
    }
}
