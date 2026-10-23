package com.example.services

import com.example.models.data.Stop
import com.example.models.routing.Connection
import com.example.models.routing.Leg
import com.example.models.routing.Network

class Planner(val network: Network) {
    fun plan(sourceStop: Stop, destinationStop: Stop, startTime: Int, minTransferTime: Int = 60): List<Leg>? {
        val earliestArrival = HashMap<String, Int>()
        val incomingConnection = HashMap<String, Connection>()
        val tripBoarding = HashMap<String, Connection>()
        earliestArrival[sourceStop.stopId] = startTime

        fun arrivalAt(stopId: String) = earliestArrival[stopId] ?: Int.MAX_VALUE

        for (connection in network.connections) {
            if (connection.departure < startTime) continue
            if (connection.departure >= arrivalAt(destinationStop.stopId)) break

            val tripId = connection.trip.tripId
            val fromStopId = connection.fromStop.stopId
            val toStopId = connection.toStop.stopId
            
            val alreadyOnTrip = tripBoarding.containsKey(tripId)
            val buffer = if (fromStopId == sourceStop.stopId) 0 else minTransferTime
            val canBoard = alreadyOnTrip || (arrivalAt(fromStopId) != Int.MAX_VALUE && arrivalAt(fromStopId) + buffer <= connection.departure)
            if (!canBoard) continue
            
            if (!alreadyOnTrip) tripBoarding[tripId] = connection

            if (connection.arrival < arrivalAt(toStopId)) {
                earliestArrival[toStopId] = connection.arrival
                incomingConnection[toStopId] = connection
            }
        }

        if (!incomingConnection.containsKey(destinationStop.stopId)) return null

        val legs = mutableListOf<Leg>()
        var stopId = destinationStop.stopId
        while (stopId != sourceStop.stopId) {
            val lastConnection = incomingConnection[stopId] ?: return null
            val boarding = tripBoarding.getValue(lastConnection.trip.tripId)
            legs.add(
                Leg(
                    trip = lastConnection.trip,
                    fromStop = boarding.fromStop,
                    toStop = lastConnection.toStop,
                    departureTime = boarding.departure,
                    arrivalTime = lastConnection.arrival
                )
            )
            stopId = boarding.fromStop.stopId
        }
        return legs.reversed()
    }
}
